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
    var PICK_IMAGE_FROM_ALBUM=0
    var storage: FirebaseStorage?= null
    var photoUri: Uri?=null
    var auth: FirebaseAuth?=null
    var firestore:FirebaseFirestore?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //Inicializar almacenamiento
        storage= FirebaseStorage.getInstance()
        auth=FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()
        //Abrir el album
        var photoPickerIntent=Intent(Intent.ACTION_PICK)
        photoPickerIntent.type="image/*"
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)

        //aniadir la imagen subida al evento
        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
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
    fun contentUpload(){
         //crear el nombre de archivo
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"
        var storageRef= storage?.reference?.child("images")?.child(imageFileName)

        storageRef?.putFile(photoUri!!)?.continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener {uri->
            var contentDTO= ContentDTO()
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
        //Metodo de callback
        /*storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                var contentDTO= ContentDTO()
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
        }*/

    }
}
