
package com.example.sanaescencia.ui.components

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import androidx.exifinterface.media.ExifInterface
import android.graphics.Matrix

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

        var cameraProvider by remember {
            mutableStateOf<ProcessCameraProvider?>(null)
        }

        DisposableEffect(cameraProvider) {
            onDispose {
                cameraProvider?.unbindAll()
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val cameraProviderFuture =
                        ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val provider = cameraProviderFuture.get()
                        cameraProvider = provider

                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

                        val capture = ImageCapture.Builder()
                            .setCaptureMode(
                                ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
                            )
                            .build()

                        imageCapture = capture

                        provider.unbindAll()

                        provider.bindToLifecycle(
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

                            val photoFile = File.createTempFile(
                                "perfil_",
                                ".jpg",
                                context.cacheDir
                            )

                            val outputOptions = ImageCapture.OutputFileOptions
                                .Builder(photoFile)
                                .build()

                            val executor = ContextCompat.getMainExecutor(context)

                            capture.takePicture(
                                outputOptions,
                                executor,

                                object : ImageCapture.OnImageSavedCallback {

                                    override fun onImageSaved(
                                        outputFileResults: ImageCapture.OutputFileResults
                                    ) {
                                        val bitmap = BitmapFactory.decodeFile(
                                            photoFile.absolutePath
                                        )

                                        if (bitmap != null) {
                                            val exif = ExifInterface(photoFile.absolutePath)

                                            val rotation = when (
                                                exif.getAttributeInt(
                                                    ExifInterface.TAG_ORIENTATION,
                                                    ExifInterface.ORIENTATION_NORMAL
                                                )
                                            ) {
                                                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                                                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                                                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                                                else -> 0f
                                            }

                                            val matrix = Matrix().apply {
                                                postRotate(rotation)
                                            }

                                            val correctedBitmap = Bitmap.createBitmap(
                                                bitmap,
                                                0,
                                                0,
                                                bitmap.width,
                                                bitmap.height,
                                                matrix,
                                                true
                                            )

                                            onPhotoCaptured(correctedBitmap)

                                            if (correctedBitmap != bitmap) {
                                                bitmap.recycle()
                                            }
                                        }

                                        photoFile.delete()
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        exception.printStackTrace()
                                        photoFile.delete()
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
