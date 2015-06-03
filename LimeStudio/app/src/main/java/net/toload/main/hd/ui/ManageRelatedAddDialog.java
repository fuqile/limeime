package net.toload.main.hd.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.toload.main.hd.R;

public class ManageRelatedAddDialog extends DialogFragment {

	private Activity activity;
	private View view;

	private ManageRelatedHandler handler;

	private Button btnManageRelatedCancel;
	private Button btnManageRelatedSave;

	private Button btnManageMinusScore;
	private Button btnManageAddScore;

	private TextView edtManageRelatedScore;

	private EditText edtManageRelatedPword;
	private EditText edtManageRelatedCword;

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public static ManageRelatedAddDialog newInstance() {
		ManageRelatedAddDialog btd = new ManageRelatedAddDialog();
						   btd.setCancelable(true);
		return btd;
	}
	
	public void setHandler(ManageRelatedHandler handler){
		this.handler = handler;
	}

	@Override
	public void onAttach(Activity act) {
		super.onAttach(act);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.setCancelable(false);
	}


	@Override
	public void onStart() {
		super.onStart();
		Dialog dialog = getDialog();
		if (dialog != null) {
			dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(android.content.DialogInterface dialog,
								 int keyCode, android.view.KeyEvent event) {
				if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
					// To dismiss the fragment when the back-button is pressed.
					dismiss();
					return true;
				}
				// Otherwise, do nothing else
				else return false;
			}
		});
	}

	public void cancelDialog(){
		this.dismiss();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {

		getDialog().getWindow().setTitle(getResources().getString(R.string.manage_related_dialog_add));

		activity = getActivity();
		view = inflater.inflate(R.layout.fragment_dialog_related_add, container, false);

		btnManageRelatedCancel = (Button) view.findViewById(R.id.btnManageRelatedCancel);
		btnManageRelatedCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancelDialog();
			}
		});

		btnManageRelatedSave = (Button) view.findViewById(R.id.btnManageRelatedSave);
		btnManageRelatedSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
				alertDialog.setTitle(activity.getResources().getString(R.string.manage_related_dialog_add));
				alertDialog.setMessage(activity.getResources().getString(R.string.manage_related_dialog_add_message));
				alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getResources().getString(R.string.dialog_confirm),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								String pword = edtManageRelatedPword.getText().toString();
								String cword = edtManageRelatedCword.getText().toString();
								String score = edtManageRelatedScore.getText().toString();
								if(!pword.isEmpty() && !cword.isEmpty()){
									pword = pword.trim();
									cword = cword.trim();
									int s = 0;
									try{
										s = Integer.parseInt(score);
									} catch(Exception e){}
									handler.addRelated(pword, cword, s);
									dialog.dismiss();
									cancelDialog();
								}else{
									Toast.makeText(activity, R.string.insert_error, Toast.LENGTH_SHORT).show();
								}
							}
						});
				alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getResources().getString(R.string.dialog_cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
				alertDialog.show();
			}
		});

		btnManageMinusScore = (Button) view.findViewById(R.id.btnManageMinusScore);
		btnManageMinusScore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					int value = Integer.parseInt(edtManageRelatedScore.getText().toString());
					if(value > 0){
						value = value -1 ;
						edtManageRelatedScore.setText(String.valueOf(value));
					}
				}catch(Exception e){}
			}
		});

		btnManageAddScore = (Button) view.findViewById(R.id.btnManageAddScore);
		btnManageAddScore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					int value = Integer.parseInt(edtManageRelatedScore.getText().toString());
					value = value + 1 ;
					edtManageRelatedScore.setText(String.valueOf(value));
				}catch(Exception e){}
			}
		});

		edtManageRelatedPword = (EditText) view.findViewById(R.id.edtManageRelatedPword);
		edtManageRelatedPword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(edtManageRelatedPword.getText() != null && !edtManageRelatedPword.getText().equals("") &&
						edtManageRelatedPword.getText().length() > 1){
					edtManageRelatedPword.setText(edtManageRelatedPword.getText().subSequence(0,1));
				}
			}
		});

		edtManageRelatedCword = (EditText) view.findViewById(R.id.edtManageRelatedCword);
		edtManageRelatedScore = (TextView) view.findViewById(R.id.edtManageRelatedScore);
		edtManageRelatedScore.setText("1");
		
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);
	}

}
