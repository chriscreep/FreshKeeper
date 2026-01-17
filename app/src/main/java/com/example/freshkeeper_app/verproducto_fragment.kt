package com.example.freshkeeper_app

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class verproducto_fragment : Fragment() {

    private lateinit var txtNombreProducto: EditText
    private lateinit var spinnerCategoria: Spinner
    private lateinit var includeFecha: View
    private lateinit var editTextFecha: EditText
    private lateinit var btnClear: ImageView
    private lateinit var btnCalendar: ImageView
    private lateinit var btnGuardar: Button
    private lateinit var btnEditar: ImageButton
    private lateinit var btnEliminar: ImageButton


    private var docId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_verproducto_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        txtNombreProducto = view.findViewById(R.id.txtnombreproducto)
        spinnerCategoria = view.findViewById(R.id.spinnercategoria)
        includeFecha = view.findViewById(R.id.includeFecha)
        editTextFecha = includeFecha.findViewById(R.id.editTextFecha)
        btnClear = includeFecha.findViewById(R.id.btnClear)
        btnCalendar = includeFecha.findViewById(R.id.btnCalendar)
        btnGuardar = view.findViewById(R.id.btnguardar)
        btnEditar = view.findViewById(R.id.btneditar)
        btnEliminar = view.findViewById(R.id.btneliminar)


        docId = arguments?.getString("docId") ?: ""
        val nombre = arguments?.getString("nombre") ?: ""
        val categoria = arguments?.getString("categoria") ?: ""
        val fechaCompra = arguments?.getString("fecha_compra") ?: ""
        val fechaVencimiento = arguments?.getString("fecha_vencimiento") ?: ""


        txtNombreProducto.setText(nombre)


        val listaCategorias = listOf(
            "Otros...",
            "Carnes y proteínas",
            "Aceites y condimentos",
            "Granos y cereales",
            "Enlatados",
            "Salsas y aderezos",
            "Frutas y verduras",
            "Snacks"
        )
        val adapterSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listaCategorias)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adapterSpinner

        val index = listaCategorias.indexOf(categoria)
        if (index >= 0) {
            spinnerCategoria.setSelection(index)
        }


        editTextFecha.setText(fechaVencimiento)


        txtNombreProducto.isEnabled = false
        spinnerCategoria.isEnabled = false
        editTextFecha.isEnabled = false
        btnCalendar.visibility = View.GONE
        btnGuardar.visibility = View.GONE
        btnEliminar.visibility = View.GONE


        btnEditar.setOnClickListener {
            txtNombreProducto.isEnabled = true
            spinnerCategoria.isEnabled = true
            editTextFecha.isEnabled = true
            btnCalendar.visibility = View.VISIBLE
            btnGuardar.visibility = View.VISIBLE
            btnEliminar.visibility = View.VISIBLE
            btnEditar.visibility = View.GONE
        }


        btnCalendar.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    calendar.set(selectedYear, selectedMonth, selectedDay)
                    val fechaSeleccionada = formato.format(calendar.time)
                    editTextFecha.setText(fechaSeleccionada)
                    btnClear.visibility = View.VISIBLE
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
        btnClear.setOnClickListener {
            editTextFecha.setText("")
            btnClear.visibility = View.GONE
        }


        btnGuardar.setOnClickListener {
            val nuevoNombre = txtNombreProducto.text.toString().trim()
            val nuevaCategoria = spinnerCategoria.selectedItem.toString()
            val nuevaFechaVencimiento = editTextFecha.text.toString().trim()

            if (nuevoNombre.isEmpty() || nuevaFechaVencimiento.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val uid = currentUser.uid

            val updatedData = hashMapOf(
                "nombre" to nuevoNombre,
                "categoria" to nuevaCategoria,
                "fechaVencimiento" to nuevaFechaVencimiento
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("usuarios")
                .document(uid)
                .collection("productos")
                .document(docId)
                .update(updatedData as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Producto actualizado", Toast.LENGTH_SHORT).show()
                    txtNombreProducto.isEnabled = false
                    spinnerCategoria.isEnabled = false
                    editTextFecha.isEnabled = false
                    btnCalendar.visibility = View.GONE
                    btnGuardar.visibility = View.GONE
                    btnEliminar.visibility = View.GONE
                    btnEditar.visibility = View.VISIBLE
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }


        btnEliminar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar producto")
                .setMessage("¿Estas seguro de que deseas eliminar este producto?")
                .setPositiveButton("Aceptar") { _, _ ->
                    if (docId.isEmpty()) {
                        Toast.makeText(requireContext(), "No se pudo identificar el producto", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser == null) {
                        Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    val uid = currentUser.uid
                    val db = FirebaseFirestore.getInstance()
                    db.collection("usuarios")
                        .document(uid)
                        .collection("productos")
                        .document(docId)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }
}
