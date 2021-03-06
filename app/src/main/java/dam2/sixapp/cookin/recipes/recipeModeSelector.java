package dam2.sixapp.cookin.recipes;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import dam2.sixapp.cookin.R;
import dam2.sixapp.cookin.recipes.activitySteps.StepsActivity;

public class recipeModeSelector extends ActionBarActivity implements View.OnClickListener{

    private TextView recipeName,difficulty,duration,votes,zone,description;
    private Button readButton,assistantButton;
    private ImageView recipeImage;


    static int idReceta;

    String conex = "http://cookin.hol.es/android_connect/";
    String web="cogerdesc.php?id=";
    private String recipeNameS,difficultyS,durationS,votesS,zoneS,descriptioS,imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_mode_selector);
        //final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);

        recipeName = (TextView) findViewById(R.id.recipeName);
        recipeImage = (ImageView) findViewById(R.id.recipeImage);
        difficulty = (TextView) findViewById(R.id.difficulty);
        duration = (TextView) findViewById(R.id.duration);
        votes = (TextView) findViewById(R.id.votes);
        zone = (TextView) findViewById(R.id.zone);
        description = (TextView) findViewById(R.id.description);
        readButton = (Button) findViewById(R.id.readButton);
        assistantButton = (Button) findViewById(R.id.assistantButton);
        Bundle b = getIntent().getExtras();
        idReceta=b.getInt("id");
        mostrar tarea=new mostrar();
        tarea.execute();
        readButton.setOnClickListener(this);
        assistantButton.setOnClickListener(this);

        recipeNameS = "";
        imageUrl = "/** AQUI URL DE IMAGEN **/";
        difficultyS = "";
        durationS = "";
        votesS = "";
        zoneS = "";
        descriptioS = "";

        recipeName.setText(recipeNameS);
        if(imageUrl == null) {
            recipeImage.setImageDrawable(getResources().getDrawable(R.drawable.app_logo));
        }else{
            Picasso.with(getApplicationContext()).load(imageUrl).into(recipeImage);
        }
        difficulty.setText(getResources().getString(R.string.difficulty)+" "+difficultyS);
        duration.setText(getResources().getString(R.string.duration)+" "+durationS);
        votes.setText(getResources().getString(R.string.votes)+" "+votesS);
        zone.setText(getResources().getString(R.string.zone)+" "+zoneS);
        description.setText(descriptioS);

        Contador con = new Contador(recipeModeSelector.this);
        con.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe_mode_selector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.favorite:
                //Accion
                return true;
            case R.id.todo:
                //Accion
                return true;
            case R.id.done:
                //Accion
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.readButton:
                Intent i=new Intent(getApplicationContext(),readMode.class);
                i.putExtra("id",idReceta);
                startActivity(i);
                break;
            case R.id.assistantButton:
                Intent u=new Intent(getApplicationContext(),StepsActivity.class);
                u.putExtra("id",idReceta);
                u.putExtra("recipeName",recipeNameS);
                startActivity(u);
                break;
        }
    }

    private class mostrar extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean resul = true;

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost del = new HttpPost(conex+web+idReceta);

            del.setHeader("content-type", "application/json");

            try {
                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());
                JSONArray respJSON = new JSONArray(respStr);
               // recetas = new String[respJSON.length()];
                //arrayid = new int[respJSON.length()];

                for (int i = 0; i < respJSON.length(); i++) {
                    JSONObject obj = respJSON.getJSONObject(i);

                    descriptioS=obj.getString("DESCRIPCION");
                    imageUrl=obj.getString("IMAGEN");
                    recipeNameS=obj.getString("NOMBRE");
                    difficultyS=obj.getString("DIFICULTAD");
                    durationS=obj.getString("DURACION");
                    votesS=obj.getString("VOTOS");
                    zoneS=obj.getString("ZONA");

                }
            } catch (Exception ex) {
                Log.e("ServicioRest", "Error!", ex);
                resul = false;
            }

            return resul;

        }


        protected void onPostExecute(Boolean result) {

            if (result) {

                    description.setText(descriptioS);
                    Picasso.with(getApplicationContext()).load(imageUrl).into(recipeImage);
                    recipeName.setText(recipeNameS);
                    difficulty.setText("Dificultad: "+difficultyS);
                    duration.setText("Duración: "+durationS+"'");
                    zone.setText("Zona: "+zoneS);
                    votes.setText("Votos: "+votesS);

            }else{
                Toast.makeText(getApplicationContext(), "Se ha producido un error al realizar la consulta", Toast.LENGTH_LONG).show();
            }
        }

    }

    public int getIdReceta() {
        return idReceta;
    }



}
