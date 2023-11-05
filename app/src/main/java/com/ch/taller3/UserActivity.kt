package com.ch.taller3

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ch.taller3.databinding.ActivityUsuarioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ch.taller3.models.User

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsuarioBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        if (user != null) {
            userId = user.uid //UID del usuario autenticado
            databaseReference = FirebaseDatabase.getInstance().reference.child("usuarios").child(userId!!)
        }

        // Cargar los datos del usuario desde Firebase
        cargarDatosUsuario()

        binding.saveButton.setOnClickListener {
            val usuario = User(
                nombre = binding.name.text.toString(),
                apellido = binding.lastName.text.toString(),
                numeroIdentificacion = binding.identificationNumber.text.toString(),
                latitud = binding.latitud.text.toString().toDouble(),
                longitud = binding.longitud.text.toString().toDouble()
            )

            //Nuevo valor en la base de datos
            databaseReference.setValue(usuario).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar datos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Cargar el email del usuario autenticado
        if (user != null) {
            binding.email.setText(user.email)
        }
    }

    /***************************************************FUNCIONES*********************************************************/
    private fun cargarDatosUsuario() {
        if (userId != null) {
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val usuario = dataSnapshot.getValue(User::class.java)

                    if (usuario != null) {
                        // Mostrar los datos del usuario en los campos de texto
                        binding.name.setText(usuario.nombre)
                        binding.lastName.setText(usuario.apellido)
                        binding.identificationNumber.setText(usuario.numeroIdentificacion)
                        binding.latitud.setText(usuario.latitud.toString())
                        binding.longitud.setText(usuario.longitud.toString())
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@UserActivity, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    /***************************************************PENDIENTES*********************************************************/
    /*
    * TODO
    * Modificar email
    * Modificar contraseña
    * */
}
