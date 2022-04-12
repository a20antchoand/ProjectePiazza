package com.example.piazza.controladores.employee.fragments.perfil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.databinding.FragmentPerfilBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PerfilFragment extends Fragment implements AuthUserSession{

    private static final int RESULT_OK = -1;
    private FragmentPerfilBinding binding;
    private View root;

    private static final int PICK_IMAGE = 100;
    Uri imageUri;

    Bitmap bitmap;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();

        return root;

    }

    private void setup() {

        mostrarDatosPerfil();

        binding.imatgePerfil.setOnClickListener(view -> openGallery());

    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){


        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();

            try {
                bitmap = getThumbnail(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("btmp  " + bitmap);


            if (bitmap != null) {

                perfil.setBitmap(bitmap);
                binding.imatgePerfil.setImageBitmap(perfil.getBitmap());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bitmapData = baos.toByteArray();

                StorageReference storageRef = STORAGE.getReference();

                StorageReference ImagesRef = storageRef.child(userAuth.getUid());

                UploadTask uploadTask = ImagesRef.putBytes(bitmapData);

                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    System.out.println("success" + ImagesRef.getPath());
                    System.out.println("success" + ImagesRef.getName());
                    System.out.println("success" + ImagesRef.getRoot().toString());

                    userAuth.setUrlPerfil(ImagesRef.getRoot() + ImagesRef.getPath());

                    GuardarUsuarioBBDD(userAuth);
                    guardarDatosGlobalesJugador(userAuth);

                }).addOnFailureListener(taskSnapshot -> {
                    System.out.println("failure");
                });
            } else {
                Toast.makeText(getContext(), "No es una imatge", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void mostrarDatosPerfil() {

        binding.email.setText(userAuth.getEmail());
        binding.nom.setText(userAuth.getNom());
        binding.cognom.setText(userAuth.getCognom());
        binding.telefon.setText(userAuth.getTelefono());
        binding.rol.setText(userAuth.getRol());
        binding.rol.setEnabled(false);
        binding.horesMensuals.setText(userAuth.getHoresMensuals());

        binding.imatgePerfil.setImageBitmap(perfil.getBitmap());

    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        InputStream input = getContext().getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > 800) ? (originalSize / 800) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = getContext().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}