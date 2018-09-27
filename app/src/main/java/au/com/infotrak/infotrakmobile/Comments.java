package au.com.infotrak.infotrakmobile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class Comments extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Button SubmitButton = ((Button)findViewById(R.id.CommentSubmit));
        Button CancelButton= ((Button)findViewById(R.id.CommentCancel));
        final EditText CommentText = ((EditText)findViewById(R.id.commentText));
        final String CurrentComment = getIntent().getData().toString();
        CommentText.setText(CurrentComment);
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.setData(Uri.parse(CommentText.getText().toString()));
                setResult(RESULT_OK, data);
                finish();
            }
        });
        CancelButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.setData(Uri.parse(CurrentComment));
                setResult(RESULT_CANCELED, data);
                finish();
            }
        });

    }
}
