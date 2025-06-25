package com.pezont.teammates.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.pezont.teammates.R
import com.pezont.teammates.domain.model.enums.BottomNavItem
import com.pezont.teammates.domain.model.Credentials
import com.pezont.teammates.ui.components.TeammatesButton
import com.pezont.teammates.ui.navigation.NavigationDestination
import com.pezont.teammates.ui.snackbar.SnackbarEvent
import com.pezont.teammates.viewmodel.AuthViewModel

object LoginDestination : NavigationDestination {
    override val route = "login"
    override val titleRes = R.string.login
}

@Composable
fun LoginField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Login",
    placeholder: String = "Enter your Login"
) {

    val focusManager = LocalFocusManager.current
    val leadingIcon = @Composable {
        Icon(
            Icons.Default.Person, contentDescription = "", tint = MaterialTheme.colorScheme.primary
        )
    }

    TextField(
        leadingIcon = leadingIcon,
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = VisualTransformation.None
    )
}

@Composable
fun PasswordField(
    value: String,
    onChange: (String) -> Unit,
    submit: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
    placeholder: String = "Enter your Password"
) {

    var isPasswordVisible by remember { mutableStateOf(false) }


    val trailingIcon = @Composable {
        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
            Icon(
                if (isPasswordVisible) Icons.Default.VisibilityOff
                else Icons.Default.Visibility,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done, keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(onDone = { submit() }),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
    )
}


@Composable
fun LoginScreen(
    onTabChange: (BottomNavItem) -> Unit,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    var credentials by remember { mutableStateOf(Credentials()) }


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ) {
        Text(
            text = stringResource(R.string.authorization),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(20.dp))

        LoginField(
            value = credentials.login,
            onChange = { data -> credentials = credentials.copy(login = data) },
            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(10.dp))

        PasswordField(
            value = credentials.pwd,
            onChange = { data -> credentials = credentials.copy(pwd = data) },
            submit = { tryLogin(onTabChange, credentials, authViewModel) },
            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(30.dp))

        TeammatesButton(
            text = stringResource(R.string.login),
            onClick = { tryLogin(onTabChange, credentials, authViewModel) },
            enabled = credentials.isNotEmpty(),
            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
        )
    }
}

fun tryLogin(
    onTabChange: (BottomNavItem) -> Unit,
    credentials: Credentials,
    authViewModel: AuthViewModel
) {
    if (credentials.isNotEmpty()) {
        onTabChange(BottomNavItem.HOME)
        authViewModel.login(credentials.login, credentials.pwd)
    } else {
        SnackbarEvent(R.string.fill_all_fields)
    }
}
