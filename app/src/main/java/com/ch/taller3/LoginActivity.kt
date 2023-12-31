package com.ch.taller3

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ch.taller3.databinding.ActivityInicioSesionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.Manifest


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInicioSesionBinding

    //Firebase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        //Inicializamos Firebase Auth
        auth = Firebase.auth

        super.onCreate(savedInstanceState)
        binding = ActivityInicioSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener(){
            if (validarCampos()) {
                if (verificarPermisosDeUbicacion()) {
                    iniciarSesion()
                } else {
                    solicitarPermisosDeUbicacion()
                }
            } else {
                Toast.makeText(this, "Llene todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
        binding.registerButton.setOnClickListener(){
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    /***************************************************FUNCIONES*********************************************************/

    /*EXTRAS*/
    private fun validarCampos(): Boolean {
        if (binding.email.text.toString().isEmpty()) {
            Toast.makeText(this, "Error en email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.password.text.toString().isEmpty() || binding.password.text.toString().length < 6) {
            Toast.makeText(this, "Error en contraseña", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    //Firebase Auth
    private fun iniciarSesion(){
        auth.signInWithEmailAndPassword(
            binding.email.text.toString(),
            binding.password.text.toString()
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Permisos de ubicacion
    private fun verificarPermisosDeUbicacion(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fineLocationPermission && coarseLocationPermission
    }

    private fun solicitarPermisosDeUbicacion() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarSesion()
            } else {
                Toast.makeText(this, "Los permisos de ubicación son necesarios para iniciar sesión.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}  