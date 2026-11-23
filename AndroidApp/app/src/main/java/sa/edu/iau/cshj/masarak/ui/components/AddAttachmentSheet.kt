package sa.edu.iau.cshj.masarak.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.foundation.BorderStroke
import sa.edu.iau.cshj.masarak.ui.screens.tasks.StudentTaskForm
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable

private val AttachmentNavy = Color(0xFF0C0D45)

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AddAttachmentSheet(
    onDismiss: () -> Unit = {},
    onImageClick: () -> Unit = {},
    onFileClick: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = null
    ) {
        AttachmentLayout(
            onImageClick = onImageClick,
            onFileClick = onFileClick
        )
    }
}


@Composable
private fun AttachmentLayout(
    onImageClick: () -> Unit = {},
    onFileClick: () -> Unit = {}
) {   Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 18.dp)
        ) {
            Box(
                Modifier.align(Alignment.CenterHorizontally).size(width = 36.dp, height = 4.dp)
                    .background(Color(0xFFD6D6DC), CircleShape)
            )
            Spacer(Modifier.height(16.dp))
            Text("Add attachment", color = AttachmentNavy, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                color = Color.White, border = BorderStroke(1.dp, Color(0xFFDEDDDA))
            ) {
                Column {

                    AttachmentOption(
                        icon = Icons.Outlined.Image,
                        label = "Image",
                        subtitle = "From your photos",
                        image = true,
                        onClick = onImageClick
                    )
                    HorizontalDivider(color = Color(0xFFE3E3E8))

                    AttachmentOption(
                        icon = Icons.Outlined.Description,
                        label = "File",
                        subtitle = "PDF or document",
                        image = false,
                        onClick = onFileClick
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable

private fun AttachmentOption(
    icon: ImageVector,
    label: String,
    subtitle: String,
    image: Boolean,
    onClick: () -> Unit = {}
) {
    Row(

        Modifier
            .fillMaxWidth()
            .heightIn(min = 66.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(36.dp).background(if (image) Color(0xFFCEF0EF) else Color(0xFFE5E7F5), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = if (image) Color(0xFF00696E) else AttachmentNavy, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, color = AttachmentNavy, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color(0xFF55555F), fontSize = 12.sp)
        }
    }
}

@Preview(name = "Add Attachment Sheet", widthDp = 390, heightDp = 844)
@Composable
private fun AttachmentSheetPreview() {
    MaterialTheme {
        Box(Modifier.fillMaxSize().background(AttachmentNavy), contentAlignment = Alignment.BottomCenter) {
            Column(Modifier.fillMaxSize()) {
                AttachmentPreviewStatusBar()
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    StudentTaskForm(filled = false)
                }
            }
            Box(Modifier.matchParentSize().background(AttachmentNavy.copy(alpha = .32f)))
            Column(Modifier.fillMaxWidth()) {
                AttachmentLayout()
                Box(
                    Modifier.fillMaxWidth().height(17.dp).background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Box(Modifier.width(100.dp).height(4.dp).background(Color(0xFFA7A7AE), CircleShape))
                }
            }
        }
    }
}

@Composable
private fun AttachmentPreviewStatusBar() {
    Box(Modifier.fillMaxWidth().height(28.dp).background(Color(0xFFE2ECF9)).padding(horizontal = 20.dp)) {
        Text("9:41", modifier = Modifier.align(Alignment.CenterStart),
            color = AttachmentNavy, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Canvas(Modifier.align(Alignment.CenterEnd).size(50.dp, 14.dp)) {
            val u = size.height / 14f
            repeat(4) { i ->
                val h = (3 + i * 2) * u
                drawRect(AttachmentNavy, Offset(i * 3 * u, 12 * u - h), Size(2 * u, h))
            }
            listOf(12f, 8f, 4f).forEach { d ->
                drawArc(AttachmentNavy, 225f, 90f, false,
                    topLeft = Offset((23f - d / 2) * u, (11f - d / 2) * u),
                    size = Size(d * u, d * u), style = Stroke(1.2f * u))
            }
            drawCircle(AttachmentNavy, u, Offset(23 * u, 11 * u))
            drawRect(AttachmentNavy, Offset(36 * u, 3 * u), Size(7 * u, 10 * u))
            drawRect(AttachmentNavy, Offset(38 * u, 1.5f * u), Size(3 * u, 2 * u))
        }
    }
}
