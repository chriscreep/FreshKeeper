package com.example.freshkeeper_app

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*



class Agregarproducto_fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_agregarproducto_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtNombreProducto = view.findViewById<EditText>(R.id.txtnombreproducto)
        val spinner = view.findViewById<Spinner>(R.id.spinnercategoria)
        val includeFecha = view.findViewById<View>(R.id.includeFecha)
        val editTextFecha = includeFecha.findViewById<EditText>(R.id.editTextFecha)
        val btnClear = includeFecha.findViewById<ImageView>(R.id.btnClear)
        val btnCalendar = includeFecha.findViewById<ImageView>(R.id.btnCalendar)
        val btnAgregar = view.findViewById<Button>(R.id.btnagregar)


        val categorias = listOf(
            "Otros...",
            "Carnes y proteínas",
            "Aceites y condimentos",
            "Granos y cereales",
            "Enlatados",
            "Salsas y aderezos",
            "Frutas y verduras",
            "Snacks"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


        btnCalendar.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->

                    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    calendar.set(selectedYear, selectedMonth, selectedDay)
                    val fechaSeleccionada = formato.format(calendar.time)
                    editTextFecha.setText(fechaSeleccionada)
                    btnClear.visibility = View.VISIBLE
                },
                year, month, day
            )

            datePickerDialog.show()
        }


        btnClear.setOnClickListener {
            editTextFecha.setText("")
            btnClear.visibility = View.GONE
        }


        editTextFecha.setOnClickListener {
            btnCalendar.performClick()
        }


        btnAgregar.setOnClickListener {
            val nombreProducto = txtNombreProducto.text.toString().trim()
            val categoria = spinner.selectedItem.toString()
            val fechaVencimiento = editTextFecha.text.toString().trim()

            if (nombreProducto.isEmpty() || fechaVencimiento.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fechaCompra = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                Toast.makeText(requireContext(), "Inicia sesión para guardar tus productos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val uid = currentUser.uid

            val productoData = hashMapOf(
                "nombre" to nombreProducto,
                "categoria" to categoria,
                "fechaCompra" to fechaCompra,
                "fechaVencimiento" to fechaVencimiento,
                "fechaCreacion" to FieldValue.serverTimestamp()
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("usuarios")
                .document(uid)
                .collection("productos")
                .add(productoData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Producto agregado correctamente", Toast.LENGTH_SHORT).show()

                    txtNombreProducto.text.clear()
                    editTextFecha.text.clear()
                    btnClear.visibility = View.GONE


                    programarNotificacion(nombreProducto, fechaVencimiento, requireContext())
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }
}
