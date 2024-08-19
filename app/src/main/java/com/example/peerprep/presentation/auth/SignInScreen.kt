package com.example.peerprep.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.peerprep.ui.theme.turquoise

@Composable
fun SignInScreen(signInViewModel: SignInViewModel = hiltViewModel(),onNavigateToSignUp: () -> Unit,onSignInSuccess: () -> Unit,onNavigateToForgotPassword: () -> Unit,) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }


    val signInStatus = signInViewModel.signInStatus.observeAsState()


    LaunchedEffect(signInStatus.value) {
        if (signInStatus.value == "Success") {
            onSignInSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sign In", fontSize = 30.sp, color = turquoise)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { signInViewModel.signIn(emailState.value, passwordState.value) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(turquoise)
        ) {
            Text("Sign In", color = Color.White)
        }


        Button(onClick = onNavigateToSignUp,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(turquoise)) {
            Text("Sign Up")
        }

        TextButton(onClick = onNavigateToForgotPassword) {
            Text("Forgot your password?", color = Color.Gray)
        }

    }
}
