package com.darax.instagrotlin.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darax.instagrotlin.R
import com.darax.instagrotlin.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    private var pick=0
    private var storage: FirebaseStorage?= null
    private var photoUri: Uri?=null
    private var auth: FirebaseAuth?=null
    private var firestore:FirebaseFirestore?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //Inicializar almacenamiento
        storage= FirebaseStorage.getInstance()
        auth=FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()
        //Abrir el album
        val photoPickerIntent=Intent(Intent.ACTION_PICK)
        photoPickerIntent.type="image/*"
        startActivityForResult(photoPickerIntent,pick)

        //aniadir datos de la imagen
        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == pick){
            if(resultCode == Activity.RESULT_OK){
                //Esta es la ruta de la imagen seleccionada
                photoUri = data?.data
                addphoto_image.setImageURI(photoUri)
            }else{
                //Salir de la seleccion de fotos si no selecciono nada
                finish()
            }
        }
    }
    private fun contentUpload(){
         //crear el nombre de archivo
        val timestamp = SimpleDateFormat.getDateInstance().format(Date())
        val imageFileName = "IMAGE_" + timestamp + "_.png"
        val storageRef= storage?.reference?.child("images")?.child(imageFileName)

        storageRef?.putFile(photoUri!!)?.continueWithTask { _: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener {uri->
            val contentDTO= ContentDTO()
            //Insertar downloadURL de la imagen
            contentDTO.imageUrl=uri.toString()
            //Insertar uid de usuario
            contentDTO.uid = auth?.currentUser?.uid
            //insertar userId
            contentDTO.userId = auth?.currentUser?.email
            //Insertar explicacion del contenido
            contentDTO.explain = addphoto_edit_explain.text.toString()
            //Insertar tiempo donde se mando
            contentDTO.timestamp = System.currentTimeMillis()
            firestore?.collection("images")?.document()?.set(contentDTO)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
