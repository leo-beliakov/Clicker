package com.apps.leo.clicker.game.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.apps.leo.clicker.R
import com.apps.leo.clicker.game.common.ui.composables.clickable.invisibleClickable
import com.apps.leo.clicker.game.common.ui.composables.text.OutlinedText
import com.apps.leo.clicker.game.ui.model.GameUiState

@Composable
fun BoostConfirmationDialog(
    boost: GameUiState.Boost,
    onConfirmed: () -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            DialogContent(
                boost = boost,
                onConfirmed = onConfirmed
            )
            CloseButton(
                onClose = onDismissRequest,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
private fun DialogContent(
    boost: GameUiState.Boost,
    onConfirmed: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(80.dp)
            )
            .border(
                width = 4.dp,
                color = Color.Blue,
                shape = RoundedCornerShape(80.dp)
            )
            .padding(24.dp)
    ) {
        Image(
            painter = painterResource(id = boost.imageResId),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(0.6f)
        )
        Button(
            border = BorderStroke(4.dp, Color.Blue),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
            shape = RoundedCornerShape(12.dp),
            onClick = onConfirmed
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_video),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
                OutlinedText(
                    text = "GET",
                    textStyle = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    strokeWidth = 3.sp
                )
            }
        }
    }
}

@Composable
private fun CloseButton(
    onClose: () -> Unit,
    modifier: Modifier
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_close),
        contentDescription = stringResource(R.string.action_close),
        modifier = modifier
            .size(55.dp)
            .background(
                color = Color.Magenta,
                shape = CircleShape
            )
            .border(
                width = 4.dp,
                color = Color.Blue,
                shape = CircleShape
            )
            .invisibleClickable(onClick = onClose)
            .padding(6.dp)
    )
}
