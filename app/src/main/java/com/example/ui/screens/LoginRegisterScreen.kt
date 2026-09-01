package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BrandPrimary
import kotlinx.coroutines.delay

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle

/**
 * A compact, modern input field designed specifically for high visual polish and
 * space-efficiency in landscape and dense displays. Replaces oversized Material text boxes
 * with a slim modern input bar with crisp iconography, responsive focus/error borders,
 * clear legibility, and integrated labels/placeholders.
 */
@Composable
fun CompactInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = when {
        isError -> Color(0xFFFF5252)
        isFocused -> Color(0xFFFFD54F)
        else -> Color(0x5590CAF9)
    }
    val backgroundColor = if (isFocused) Color(0x33132238) else Color(0x22000000)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = if (isError) Color(0xFFFF5252) else if (isFocused) Color(0xFFFFD54F) else Color(0xFF90CAF9),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.3.sp,
            modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .border(
                    width = if (isFocused || isError) 1.5.dp else 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .testTag(testTag),
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            ),
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(Color(0xFFFFD54F)),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 10.dp, end = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        leadingIcon()
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = Color(0xFF78909C),
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        }
                        innerTextField()
                    }
                    if (trailingIcon != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            trailingIcon()
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun LoginRegisterScreen(
    currentLanguage: String,
    isAudioMuted: Boolean,
    errorMessage: String?,
    onCheckUsernameTaken: suspend (String) -> Boolean,
    onLogin: (username: String, pass: String) -> Unit,
    onRegister: (username: String, pass: String) -> Unit,
    onToggleLanguage: () -> Unit,
    onToggleAudioMute: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Log In, 1 = Register
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    var isUsernameTaken by remember { mutableStateOf(false) }
    var isCheckingUsername by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Real-time username availability debounce check (Room query)
    LaunchedEffect(username, selectedTab) {
        val clean = username.trim()
        if (selectedTab == 1 && clean.length >= 3) {
            delay(350)
            isCheckingUsername = true
            isUsernameTaken = onCheckUsernameTaken(clean)
            isCheckingUsername = false
        } else {
            isUsernameTaken = false
            isCheckingUsername = false
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Fullscreen scenic background artwork
        Image(
            painter = painterResource(id = R.drawable.bg_main_menu),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark gradient overlay for crystal clear contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xBB0D1B2A),
                            Color(0xDD132238),
                            Color(0xF50F172A)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
        // Top right Controls: Circular Language Toggle (EN/ID) and Audio Mute matching Main Menu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Language Toggle: Circular button (EN / ID, defaulting to EN)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clickable(onClick = onToggleLanguage)
                    .testTag("language_toggle_btn"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0x44000000))
                        .border(1.2.dp, Color(0x88FFD54F), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (currentLanguage == "in") "ID" else "EN",
                        color = Color(0xFFFFD54F),
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Audio Mute: Circular button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clickable(onClick = onToggleAudioMute)
                    .testTag("audio_mute_btn"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0x44000000))
                        .border(
                            1.2.dp,
                            if (isAudioMuted) Color(0x88FF5252) else Color(0x884FCB8F),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isAudioMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = stringResource(if (isAudioMuted) R.string.audio_unmute else R.string.audio_mute),
                        tint = if (isAudioMuted) Color(0xFFFF5252) else Color(0xFF4FCB8F),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Side-by-side Landscape Layout: Occupies strictly the remaining space below the top row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Hero Banner: Bang Patuh Ninja Character + Brand Title
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mascot_owl_transparent),
                    contentDescription = "Bang Patuh Ninja",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(130.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "BANG PATUH",
                    color = Color(0xFFBC851C),
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = stringResource(R.string.auth_title),
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            // Right Form Column: Sized to fit compactly and neatly around content
            Card(
                modifier = Modifier
                    .weight(1.05f)
                    .widthIn(max = 340.dp)
                    .wrapContentHeight()
                    .border(1.dp, Color(0x3364B5F6), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xDD132238)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Segmented Control: Proportionate, compact 32dp height, clear visual pill
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33000000))
                            .border(1.dp, Color(0x3364B5F6), RoundedCornerShape(8.dp))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Login Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selectedTab == 0) Color(0x33FFD54F) else Color.Transparent)
                                .border(
                                    width = if (selectedTab == 0) 1.dp else 0.dp,
                                    color = if (selectedTab == 0) Color(0x99FFD54F) else Color.Transparent,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                    selectedTab = 0
                                    isUsernameTaken = false
                                }
                                .testTag("tab_login"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.auth_login_tab),
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 0) Color(0xFFFFD54F) else Color(0xFF90CAF9),
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Register Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selectedTab == 1) Color(0x33FFD54F) else Color.Transparent)
                                .border(
                                    width = if (selectedTab == 1) 1.dp else 0.dp,
                                    color = if (selectedTab == 1) Color(0x99FFD54F) else Color.Transparent,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                    selectedTab = 1
                                }
                                .testTag("tab_register"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.auth_register_tab),
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 1) Color(0xFFFFD54F) else Color(0xFF90CAF9),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Compact Username Input
                    CompactInputField(
                        value = username,
                        onValueChange = { input ->
                            val filtered = input.filter { it.isLetterOrDigit() }.take(10)
                            username = filtered
                        },
                        label = stringResource(R.string.auth_username_label),
                        placeholder = stringResource(R.string.auth_username_placeholder),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (selectedTab == 1 && isUsernameTaken) Color(0xFFFF5252) else Color(0xFF64B5F6),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = if (selectedTab == 1 && username.trim().length >= 3 && !isCheckingUsername && !isUsernameTaken) {
                            {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Available",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null,
                        isError = selectedTab == 1 && isUsernameTaken,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        testTag = "username_input"
                    )

                    // Helper label & character counter
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedTab == 1 && isUsernameTaken) {
                                stringResource(R.string.auth_error_username_taken)
                            } else {
                                stringResource(R.string.auth_username_hint)
                            },
                            color = if (selectedTab == 1 && isUsernameTaken) Color(0xFFFF5252) else Color(0xFF90CAF9),
                            fontSize = 10.5.sp,
                            fontWeight = if (selectedTab == 1 && isUsernameTaken) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${username.length}/10",
                            color = if (username.length == 10) Color(0xFFFFC857) else Color(0xFF78909C),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Compact Password Input
                    CompactInputField(
                        value = password,
                        onValueChange = { password = it },
                        label = stringResource(R.string.auth_password_label),
                        placeholder = stringResource(R.string.auth_password_placeholder),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF64B5F6),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { showPassword = !showPassword },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("toggle_password_visibility")
                            ) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showPassword) "Hide password" else "Show password",
                                    tint = Color(0xFF90CAF9),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                val canSubmit = if (selectedTab == 1) {
                                    username.isNotBlank() && password.isNotBlank() && !isUsernameTaken && !isCheckingUsername
                                } else {
                                    username.isNotBlank() && password.isNotBlank()
                                }
                                if (canSubmit) {
                                    if (selectedTab == 0) onLogin(username, password) else onRegister(username, password)
                                }
                            }
                        ),
                        testTag = "password_input"
                    )

                    // General Error Message
                    if (!errorMessage.isNullOrBlank()) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .testTag("auth_error_text")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val canSubmit = if (selectedTab == 1) {
                        username.isNotBlank() && password.isNotBlank() && !isUsernameTaken && !isCheckingUsername
                    } else {
                        username.isNotBlank() && password.isNotBlank()
                    }

                    // Primary Submit Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            if (selectedTab == 0) {
                                onLogin(username, password)
                            } else {
                                onRegister(username, password)
                            }
                        },
                        enabled = canSubmit,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFC857),
                            contentColor = Color(0xFF0F172A),
                            disabledContainerColor = Color(0x44FFC857),
                            disabledContentColor = Color(0x77FFFFFF)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("auth_submit_btn")
                    ) {
                        Text(
                            text = stringResource(
                                if (selectedTab == 0) R.string.auth_login_btn else R.string.auth_register_btn
                            ),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    // Toggle mode text link
                    TextButton(
                        onClick = {
                            selectedTab = if (selectedTab == 0) 1 else 0
                            isUsernameTaken = false
                        },
                        modifier = Modifier.testTag("auth_toggle_mode_btn")
                    ) {
                        Text(
                            text = stringResource(
                                if (selectedTab == 0) R.string.auth_toggle_to_register else R.string.auth_toggle_to_login
                            ),
                            color = Color(0xFF90CAF9),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
}
