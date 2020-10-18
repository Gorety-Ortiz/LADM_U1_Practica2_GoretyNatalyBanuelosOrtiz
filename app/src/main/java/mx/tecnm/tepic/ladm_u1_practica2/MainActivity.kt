package mx.tecnm.tepic.ladm_u1_practica2

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ////////BOTON GUARDAR EN MEMORIA INTERNA\\\\\\\\
        guardar.setOnClickListener {
            if (rbinterna.isChecked) {
                if(guardarMemoriaInterna()==true)
                {
                    AlertDialog.Builder(this).setTitle("ATENCIÓN")
                        .setMessage("SE GUARDO EN MEMORIA INTERNA")
                        .setPositiveButton("ok"){d,i->d.dismiss()}
                        .show()
                }
                else
                {
                    AlertDialog.Builder(this).setTitle("ALERTA")
                        .setMessage("ERROR, LOS DATOS NO FUERON GUARDADOS")
                        .setPositiveButton("ok"){d,i->d.dismiss()}
                        .show()
                }
            }
            ////////BOTON ABIR EN MEMORIA EXTERNA\\\\\\\\
            if (rbsd.isChecked)
            {
                if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED
                )
                {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        0 )
                }
                if(guardarSD()==true)
                {
                    AlertDialog.Builder(this).setTitle("ATENCIÓN")
                        .setMessage("SE GUARDO EN MEMORIA SD")
                        .setPositiveButton("ok"){d,i->d.dismiss()}
                        .show()
                }
                else
                {
                    AlertDialog.Builder(this).setTitle("ALERTA")
                        .setMessage("ERROR, LOS DATOS NO FUERON GUARDADOS")
                        .setPositiveButton("ok"){d,i->d.dismiss()}
                        .show()
                }
            }
            nombrar.setText("")
            escribirf.setText("")

        }

        ////////BOTON ABRIR ARCHIVO EN MEMORIA SD\\\\\\\\
        if (rbsd.isChecked)
        {
            if(abrirSD()==true)
            {
                AlertDialog.Builder(this).setTitle("ATENCIÓN")
                    .setMessage("Buscando")
                    .setPositiveButton("ok"){d,i->d.dismiss()}
                    .show()
            }
            else
            {
                AlertDialog.Builder(this).setTitle("ALERTA")
                    .setMessage("ERROR, El archivo no fue encontrado")
                    .setPositiveButton("ok"){d,i->d.dismiss()}
                    .show()
            }
        }
        
        ////////BOTON ABRIR ARCHIVO EN MEMORIA INTERNA\\\\\\\\
        abrir.setOnClickListener {
            if (rbinterna.isChecked) {
                if(abrirMemoriaInterna()==true)
                {
                    AlertDialog.Builder(this).setTitle("ATENCIÓN")
                        .setMessage("Buscando")
                        .setPositiveButton("ok"){d,i->d.dismiss()}
                        .show()
                }
                else
                {
                    AlertDialog.Builder(this).setTitle("ALERTA")
                        .setMessage("ERROR, El archivo no fue encontrado")
                        .setPositiveButton("ok"){d,i->d.dismiss()}
                        .show()
                }
            }

        }
    }

    ////////BOTON GUARDAR EN MEMORIA INTERNA\\\\\\\\
    private fun guardarMemoriaInterna(): Boolean
    {
        try
        {
            var texto = escribirf.text.toString()
            var archivo = nombrar.text.toString()

            var fSalida = OutputStreamWriter(openFileOutput(archivo, MODE_PRIVATE))
            fSalida.write(texto)
            fSalida.flush()
            fSalida.close()
        }
        catch (io: IOException)
        {
            return false
        }
        return true;
    }

    ////////BOTON ABRIR EN MEMORIA INTERNA\\\\\\\\
    private fun abrirMemoriaInterna(): Boolean
    {
        if (fileList().contains(nombrar.text.toString()))
        {
            try
            {
                val archivo = InputStreamReader(openFileInput(nombrar.text.toString()))
                val br = BufferedReader(archivo)
                var linea = br.readLine()
                val todo = StringBuilder()
                while (linea != null) {
                    todo.append(linea + "\n")
                    linea = br.readLine()
                }
                br.close()
                archivo.close()
                AlertDialog.Builder(this).setTitle("DATOS ALMACENADOS:")
                    .setMessage(todo)
                    .setPositiveButton("ok") { d, i -> d.dismiss() }
                    .show()
            }
            catch (e: IOException)
            {
                AlertDialog.Builder(this).setTitle("ALERTA")
                    .setMessage("ERROR, ARCHIVO NO ENCONTRADO")
                    .setPositiveButton("OK") {d,i-> d.dismiss()}
                    .show()
                return false
            }
        }
        else
        {
            return false
        }
        return true
    }

    ////////BOTON GUARDAR EN MEMORIA SD\\\\\\\\
    private fun guardarSD() :Boolean
    {
        try
        {
            if(Environment.getExternalStorageState()!= Environment.MEDIA_MOUNTED)
            {
                AlertDialog.Builder(this).setTitle("ALERTA")
                    .setMessage("ERROR, No se detectó memoria externa")
                    .setPositiveButton("OK"){d,i->d.dismiss()}
                    .show()
                return false
            }
            var texto = escribirf.text.toString()
            var archivo = nombrar.text.toString()

            var rutaSD= Environment.getExternalStorageDirectory()
            var archivoSD= File(rutaSD.absolutePath,archivo)

            //VERIFICACION DE EXISTENCIA DE ARCHIVO
            if(!archivoSD.exists())
            {
                var fSalida= OutputStreamWriter(FileOutputStream(archivoSD))
                fSalida.write(texto)
                fSalida.flush()
                fSalida.close()
            }
            else
            {
                AlertDialog.Builder(this).setTitle("ALERTA")
                    .setMessage("ERROR, El archivo se sobreescribió")
                    .setPositiveButton("OK"){d,i->d.dismiss()}
                    .show()
                var fSalida= OutputStreamWriter(FileOutputStream(archivoSD))
                fSalida.write(texto)
                fSalida.flush()
                fSalida.close()
            }

        }
        catch (io:Exception)
        {
            AlertDialog.Builder(this).setTitle("ALERTA")
                .setMessage("ERROR, al guardar")
                .setPositiveButton("OK"){d,i->d.dismiss()}
                .show()
            return false
        }
        return true
    }

    ////////BOTON ABRI DESDE MEMORIA SD\\\\\\\\
    private fun abrirSD(): Boolean
    {
        try
        {
            ////////VERIFICACIÓN\\\\\\\\
            if(Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED)
            {
                AlertDialog.Builder(this).setTitle("ALERTA")
                    .setMessage("ERROR, en lectura de memoria SD")
                    .setPositiveButton("OK") {d,i->d.dismiss()}
                    .show()
                return false
            }
            var archivo = nombrar.text.toString()
            var rutaSD= Environment.getExternalStorageDirectory()
            var archivoSD= File(rutaSD.absolutePath,archivo)
            if(archivoSD.exists())
            {
                val leer = InputStreamReader(FileInputStream(archivoSD))
                val br = BufferedReader(leer)
                var linea = br.readLine()
                val todo = StringBuilder()
                while (linea != null) {
                    todo.append(linea + "\n")
                    linea = br.readLine()
                }
                br.close()
                leer.close()
                AlertDialog.Builder(this).setTitle("DATOS ALMACENADOS:")
                    .setMessage(todo)
                    .setPositiveButton("ok") { d, i -> d.dismiss() }
                    .show()
            }
            else
            {
                AlertDialog.Builder(this).setTitle("ALERTA")
                    .setMessage("ERROR ARCHIVO NO ENCONTRADO")
                    .setPositiveButton("OK") {d,i-> d.dismiss()}
                    .show()
            }
        }
        catch (IO: java.lang.Exception)
        {
            AlertDialog.Builder(this).setTitle("ALERTA")
                .setMessage("Error en el archivo")
                .setPositiveButton("OK"){d,i->d.dismiss()}
                .show()
            return false
        }
        return true





    }
}