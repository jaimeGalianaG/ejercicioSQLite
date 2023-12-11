package jaime.galiana.ejerciciosqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;

import jaime.galiana.ejerciciosqlite.configuraciones.Configuracion;
import jaime.galiana.ejerciciosqlite.databinding.ActivityMainBinding;
import jaime.galiana.ejerciciosqlite.helpers.AlumnosHelper;
import jaime.galiana.ejerciciosqlite.modelos.Alumno;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AlumnosHelper helper;
    private Dao<Alumno, Integer> daoALumno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        helper = new AlumnosHelper(this, Configuracion.BD_NAME, null, Configuracion.BD_VERSION);

        if (helper != null){
            daoALumno = helper.getDaoAlumnos();
        }

        actualizarNotaFinal();

        binding.btnInsertarMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarAlumno();
            }
        });

        binding.btnConsultarMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.txtPosicionMain.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Falta el identificador", Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        Alumno a = daoALumno.queryForId(Integer.parseInt(binding.txtPosicionMain.getText().toString()));
                        if (a == null){
                            Toast.makeText(MainActivity.this, "No hay ningun alumno con ese ID", Toast.LENGTH_SHORT).show();
                        }else{
                            binding.txtNombreMain.setText(a.getNombre());
                            binding.txtApellidosMain.setText(a.getApellidos());
                            binding.txtNota1Main.setText(Configuracion.NF.format(a.getNota1()));
                            binding.txtNota2Main.setText(Configuracion.NF.format(a.getNota2()));
                            binding.txtNota3Main.setText(Configuracion.NF.format(a.getNota3()));
                            binding.lbNotaFInalMain.setText("Nota Final: "+Configuracion.NF.format((a.getNotaFinal())));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        binding.btnModificarMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarAlumno();
            }
        });

        binding.btnEliminarMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarAlumno();
            }
        });
    }

    private void eliminarAlumno() {
        if (binding.txtPosicionMain.getText().toString().isEmpty()){
            Toast.makeText(this, "FALTA INFO", Toast.LENGTH_SHORT).show();
        }else{
            try {
                daoALumno.deleteById(Integer.parseInt(binding.txtPosicionMain.getText().toString()));
                clear();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void modificarAlumno() {
        if (binding.txtPosicionMain.getText().toString().isEmpty()){
            Toast.makeText(MainActivity.this, "Falta el identificador", Toast.LENGTH_SHORT).show();
        }else{
            try {
                Alumno a = daoALumno.queryForId(Integer.parseInt(binding.txtPosicionMain.getText().toString()));
                if (a == null){
                    Toast.makeText(MainActivity.this, "No hay ningun alumno con ese ID", Toast.LENGTH_SHORT).show();
                }else{
                    a.setNombre(binding.txtNombreMain.getText().toString());
                    a.setApellidos(binding.txtApellidosMain.getText().toString());

                    a.setNota1(Float.parseFloat(binding.txtNota1Main.getText().toString()));
                    a.setNota2(Float.parseFloat(binding.txtNota2Main.getText().toString()));
                    a.setNota3(Float.parseFloat(binding.txtNota3Main.getText().toString()));
                    daoALumno.update(a);
                    clear();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void actualizarNotaFinal() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float nota1 = Float.parseFloat(binding.txtNota1Main.getText().toString());
                    float nota2 = Float.parseFloat(binding.txtNota2Main.getText().toString());
                    float nota3 = Float.parseFloat(binding.txtNota3Main.getText().toString());
                    Float notaFinal = (nota1 + nota2 + nota3) / 3;

                    binding.lbNotaFInalMain.setText("Nota Final: " + Configuracion.NF.format(notaFinal));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        binding.txtNota1Main.addTextChangedListener(textWatcher);
        binding.txtNota2Main.addTextChangedListener(textWatcher);
        binding.txtNota3Main.addTextChangedListener(textWatcher);
    }

    private void insertarAlumno() {
        if (binding.txtNombreMain.getText().toString().isEmpty() ||
                binding.txtApellidosMain.getText().toString().isEmpty() ||
                binding.txtNota1Main.getText().toString().isEmpty() ||
                binding.txtNota2Main.getText().toString().isEmpty() ||
                binding.txtNota3Main.getText().toString().isEmpty()){
            Toast.makeText(this, "FALTAN DATOS", Toast.LENGTH_SHORT).show();
        }else{
            Alumno a = new Alumno();

            a.setNombre(binding.txtNombreMain.getText().toString());
            a.setApellidos(binding.txtApellidosMain.getText().toString());
            a.setNota1(Float.parseFloat(binding.txtNota1Main.getText().toString()));
            a.setNota2(Float.parseFloat(binding.txtNota2Main.getText().toString()));
            a.setNota3(Float.parseFloat(binding.txtNota3Main.getText().toString()));

            try {
                daoALumno.create(a);
                a.setId(daoALumno.extractId(a));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            clear();
        }
    }
    private void clear(){
        binding.txtNombreMain.setText("");
        binding.txtApellidosMain.setText("");
        binding.txtNota1Main.setText("");
        binding.txtNota2Main.setText("");
        binding.txtNota3Main.setText("");
        binding.txtPosicionMain.setText("");
        binding.lbNotaFInalMain.setText("Nota Final: ");
    }
}