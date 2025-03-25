package com.example.gimmi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.widget.*
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.view.animation.AnimationUtils
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var etInput: EditText
    private lateinit var btnSend: Button
    private lateinit var tvResponse: TextView
    private lateinit var cameraManager: CameraManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etInput = findViewById(R.id.etInput)
        btnSend = findViewById(R.id.btnSend)
        tvResponse = findViewById(R.id.tvResponse)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        btnSend.setOnClickListener {
            val userInput = etInput.text.toString().trim().lowercase()
            handleCommand(userInput)
        }

        requestCallPermission() // Request call permission at startup
    }

    private fun handleCommand(command: String) {
        when {
            command.contains("turn on flashlight") -> toggleFlashlight(true)
            command.contains("turn off flashlight") -> toggleFlashlight(false)
            command.startsWith("call ") -> {
                val phoneNumber = command.removePrefix("call ").trim()
                manageDialer(phoneNumber)
            }
            command.startsWith("calculate ") -> {
                val expression = command.removePrefix("calculate ").trim()
                tvResponse.text = calculate(expression)
            }
            else -> tvResponse.text = "Command not recognized."
        }
    }

    private fun toggleFlashlight(state: Boolean) {
        try {
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, state)
            tvResponse.text = if (state) "Flashlight ON" else "Flashlight OFF"
        } catch (e: Exception) {
            tvResponse.text = "Flashlight not available."
        }
    }

    private fun manageDialer(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")
        try {
            startActivity(intent)
        } catch (e: SecurityException) {
            tvResponse.text = "Please grant call permission."
        } catch (e: Exception) {
            tvResponse.text = "Invalid phone number."
        }
    }

    private fun calculate(expression: String): String {
        return try {
            val result = ExpressionBuilder(expression).build().evaluate()
            "Result: ${result.toString()}"
        } catch (e: Exception) {
            "Invalid Calculation"
        }
    }


    private fun requestCallPermission() {
        if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CALL_PHONE), 1)
        }
    }

    private fun showResponse(response: String) {
        tvResponse.text = response
        tvResponse.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
    }

}
