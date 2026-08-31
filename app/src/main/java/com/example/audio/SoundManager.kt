package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.sin

/**
 * SoundManager provides low-latency arcade sound playback using Android SoundPool.
 *
 * Implements the requested `playSfx(name: String)` API, and synthesizes clean 44.1kHz
 * arcade WAV waveforms on first launch so all sound effects play audibly out-of-the-box
 * without requiring external sound files.
 */
class SoundManager(private val context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(8)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val soundMap = mutableMapOf<String, Int>()
    private var isLoaded = false
    var isMuted: Boolean = false

    init {
        initializeProceduralAudio()
    }

    /**
     * Requested API: Play a short sound effect by key name.
     * Supported names:
     * - "slice-hit"
     * - "wrong-slice"
     * - "shield-bonus"
     * - "game-over"
     * - "trap-hit"
     */
    fun playSfx(name: String) {
        if (isMuted) return
        val soundId = soundMap[name]
        if (soundId != null && soundId > 0) {
            try {
                soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
            } catch (e: Exception) {
                Log.w("SoundManager", "Error playing sound: $name", e)
            }
        }
    }

    private fun initializeProceduralAudio() {
        try {
            val cacheDir = context.cacheDir
            val sampleRate = 44100

            // 1. "slice-hit": crisp high-frequency blade slash swoosh (100ms sweep from 1400Hz to 600Hz)
            val sliceFile = File(cacheDir, "sfx_slice_hit.wav")
            generateWav(sliceFile, sampleRate, durationSec = 0.11f) { t, dur ->
                val freq = 1400.0 - (t / dur) * 900.0
                val env = 1.0 - (t / dur)
                (sin(2.0 * PI * freq * t) * env * 0.9).toFloat()
            }
            soundMap["slice-hit"] = soundPool.load(sliceFile.absolutePath, 1)

            // 2. "wrong-slice": low dissonance error buzz (180ms pulse at 130Hz)
            val wrongFile = File(cacheDir, "sfx_wrong_slice.wav")
            generateWav(wrongFile, sampleRate, durationSec = 0.18f) { t, dur ->
                val env = (1.0 - (t / dur)).coerceAtLeast(0.0)
                val square = if (sin(2.0 * PI * 130.0 * t) > 0) 0.6 else -0.6
                val buzz = if (sin(2.0 * PI * 195.0 * t) > 0) 0.3 else -0.3
                ((square + buzz) * env).toFloat()
            }
            soundMap["wrong-slice"] = soundPool.load(wrongFile.absolutePath, 1)

            // 3. "shield-bonus": ascending crystal chime arpeggio (300ms, 523Hz -> 659Hz -> 784Hz -> 1046Hz)
            val shieldFile = File(cacheDir, "sfx_shield_bonus.wav")
            generateWav(shieldFile, sampleRate, durationSec = 0.32f) { t, dur ->
                val step = (t / 0.08f).toInt().coerceIn(0, 3)
                val noteFreq = when (step) {
                    0 -> 523.25 // C5
                    1 -> 659.25 // E5
                    2 -> 783.99 // G5
                    else -> 1046.50 // C6
                }
                val localT = t - step * 0.08f
                val env = (1.0 - (localT / 0.08f)).coerceIn(0.0, 1.0)
                (sin(2.0 * PI * noteFreq * t) * env * 0.85).toFloat()
            }
            soundMap["shield-bonus"] = soundPool.load(shieldFile.absolutePath, 1)

            // 4. "game-over": retro dramatic descending chime (400ms)
            val gameOverFile = File(cacheDir, "sfx_game_over.wav")
            generateWav(gameOverFile, sampleRate, durationSec = 0.42f) { t, dur ->
                val freq = 440.0 - (t / dur) * 220.0
                val env = (1.0 - (t / dur)).coerceAtLeast(0.0)
                (sin(2.0 * PI * freq * t) * env * 0.8).toFloat()
            }
            soundMap["game-over"] = soundPool.load(gameOverFile.absolutePath, 1)

            // 5. "trap-hit": dull spring wobble / hollow warning boing (250ms)
            val trapHitFile = File(cacheDir, "sfx_trap_hit.wav")
            generateWav(trapHitFile, sampleRate, durationSec = 0.25f) { t, dur ->
                val freq = 280.0 + sin(2.0 * PI * 24.0 * t) * 80.0
                val env = (1.0 - (t / dur)).coerceAtLeast(0.0)
                (sin(2.0 * PI * freq * t) * env * 0.75).toFloat()
            }
            soundMap["trap-hit"] = soundPool.load(trapHitFile.absolutePath, 1)

            isLoaded = true
        } catch (e: Exception) {
            Log.w("SoundManager", "Procedural sound generation warning", e)
        }
    }

    private fun generateWav(
        file: File,
        sampleRate: Int,
        durationSec: Float,
        sampleGenerator: (time: Double, duration: Double) -> Float
    ) {
        val numSamples = (sampleRate * durationSec).toInt()
        val dataSize = numSamples * 2 // 16-bit mono

        FileOutputStream(file).use { fos ->
            // Write 44-byte WAV header placeholder
            val header = ByteArray(44)
            fos.write(header)

            val buffer = ByteBuffer.allocate(numSamples * 2).order(ByteOrder.LITTLE_ENDIAN)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val sample = sampleGenerator(t, durationSec.toDouble()).coerceIn(-1.0f, 1.0f)
                val shortVal = (sample * 32767).toInt().toShort()
                buffer.putShort(shortVal)
            }
            fos.write(buffer.array())
        }

        // Fill real WAV header lengths
        RandomAccessFile(file, "rw").use { raf ->
            raf.seek(0)
            raf.writeBytes("RIFF")
            raf.writeInt(Integer.reverseBytes(36 + dataSize))
            raf.writeBytes("WAVE")
            raf.writeBytes("fmt ")
            raf.writeInt(Integer.reverseBytes(16)) // PCM chunk size
            raf.writeShort(java.lang.Short.reverseBytes(1.toShort()).toInt()) // AudioFormat = PCM
            raf.writeShort(java.lang.Short.reverseBytes(1.toShort()).toInt()) // Channels = 1 (Mono)
            raf.writeInt(Integer.reverseBytes(sampleRate))
            raf.writeInt(Integer.reverseBytes(sampleRate * 2)) // Byte rate
            raf.writeShort(java.lang.Short.reverseBytes(2.toShort()).toInt()) // Block align
            raf.writeShort(java.lang.Short.reverseBytes(16.toShort()).toInt()) // Bits per sample
            raf.writeBytes("data")
            raf.writeInt(Integer.reverseBytes(dataSize))
        }
    }

    fun release() {
        try {
            soundPool.release()
        } catch (e: Exception) {
            // ignore
        }
    }
}
