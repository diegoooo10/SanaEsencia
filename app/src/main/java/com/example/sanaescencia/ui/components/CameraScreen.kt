
package com.example.sanaescencia.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor

@Composable
fun CameraScreen(
    onPhotoCaptured: (Bitmap) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        var imageCapture by remember {
            mutableStateOf<ImageCapture?>(null)
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)

                    val cameraProviderFuture =
                        ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

                        val capture = ImageCapture.Builder()
                            .setCaptureMode(
                                ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
                            )
                            .build()

                        imageCapture = capture

                        cameraProvider.unbindAll()

                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            capture
                        )
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = onClose) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        imageCapture?.let { capture ->
                            val executor: Executor =
                                ContextCompat.getMainExecutor(context)

                            val outputStream = ByteArrayOutputStream()

                            capture.takePicture(
                                executor,
                                object : ImageCapture.OnImageCapturedCallback() {
                                    override fun onCaptureSuccess(
                                        image: androidx.camera.core.ImageProxy
                                    ) {
                                        val buffer = image.planes[0].buffer
                                        val bytes = ByteArray(buffer.remaining())
                                        buffer.get(bytes)

                                        val bitmap = BitmapFactory.decodeByteArray(
                                            bytes,
                                            0,
                                            bytes.size
                                        )

                                        image.close()

                                        if (bitmap != null) {
                                            onPhotoCaptured(bitmap)
                                        }
                                    }

                                    override fun onError(
                                        exception: ImageCaptureException
                                    ) {
                                        exception.printStackTrace()
                                    }
                                }
                            )
                        }
                    }
                ) {
                    Text("Tomar fotografía")
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Necesitamos permiso para utilizar la cámara.")

            Button(onClick = onClose) {
                Text("Volver")
            }
        }
    }
}
