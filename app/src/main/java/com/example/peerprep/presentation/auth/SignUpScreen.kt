package com.example.peerprep.presentation.auth



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.peerprep.ui.theme.turquoise
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel



@Composable
fun SignUpScreen(signUpViewModel: SignUpViewModel = hiltViewModel()) {
    val state = signUpViewModel.state.value
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sign Up", fontSize = 30.sp, color = turquoise)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = state.name,
            onValueChange = { signUpViewModel.onNameChanged(it) },
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)), // Add this line to make corners rounded
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        )
        ValidationMessage(state.isNameValid, "Full Name must be within 15 characters.")
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = state.username,
            onValueChange = { signUpViewModel.onUsernameChanged(it) },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        )
        ValidationMessage(state.isUsernameValid, "Username must be within 10 characters.")
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = state.email,
            onValueChange = { signUpViewModel.onEmailChanged(it) },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        )
        ValidationMessage(state.isEmailValid, "Email must be formatted correctly and contain @.")
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = state.password,
            onValueChange = { signUpViewModel.onPasswordChanged(it) },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            visualTransformation = if (signUpViewModel.passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            trailingIcon = {
                IconButton(onClick = { signUpViewModel.passwordVisibility.value = !signUpViewModel.passwordVisibility.value }) {
                    Icon(
                        imageVector = if (signUpViewModel.passwordVisibility.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (signUpViewModel.passwordVisibility.value) "Hide password" else "Show password"
                    )
                }
            }
        )
        ValidationMessage(state.isPasswordValid, "Password must be at least 6 characters.")
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = state.confirmPassword,
            onValueChange = { signUpViewModel.onConfirmPasswordChanged(it) },
            label = { Text("Confirm Password") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            visualTransformation = if (signUpViewModel.confirmPasswordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            trailingIcon = {
                IconButton(onClick = { signUpViewModel.confirmPasswordVisibility.value = !signUpViewModel.confirmPasswordVisibility.value }) {
                    Icon(
                        imageVector = if (signUpViewModel.confirmPasswordVisibility.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (signUpViewModel.confirmPasswordVisibility.value) "Hide password" else "Show password"
                    )
                }
            }
        )
        ValidationMessage(state.doPasswordsMatch, "Passwords should match.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { signUpViewModel.onSignUpClicked(context) },  // No context needed here
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(turquoise)
        ) {
            Text("Sign Up")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { /* Navigate to Login */ }) {
            Text("You already have an account? Sign in", color = Color.Gray)
        }
    }
}


@Composable
fun ValidationMessage(isValid: Boolean, message: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
    ) {
        Icon(
            imageVector = if (isValid) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null, // Decorative icon
            tint = if (isValid) Color.Green else Color.Red
        )
        Text(
            text = message,
            color = if (isValid) Color.Green else Color.Red,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

