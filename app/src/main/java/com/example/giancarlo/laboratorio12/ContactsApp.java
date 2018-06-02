package com.example.giancarlo.laboratorio12;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class ContactsApp extends AppCompatActivity {

    Button contactAddButton;
    ListView listContacts;

    ArrayList<Contacts> arrayListContact;
    ContactsAdapter contactAdapter;
    Contacts contacts;
    ArrayAdapter mArrayAdapter;

    final int C_View=1,C_Delete=2,C_Send=3,C_SMS=4,C_CALL=5,C_AUDIO=6,C_VIDEO=7,C_SEEVIDEO=8;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    VideoView mVideoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_app);

        arrayListContact=new ArrayList<Contacts>();

        listContacts= (ListView) findViewById(R.id.listView);

        contactAddButton= (Button) findViewById(R.id.contactAddButton);

        mVideoView= (VideoView) findViewById(R.id.id_video);

        //add button listener
        contactAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ContactsApp.this, Data.class);
                startActivityForResult(intent, 1);


            }
        });

        contactAdapter=new ContactsAdapter(ContactsApp.this,arrayListContact);

        listContacts.setAdapter(contactAdapter);

        listContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                registerForContextMenu(listContacts);

            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.listView) {
            menu.add(0, C_View, 1, "VIEW");
            menu.add(0, C_Delete, 2, "DELETE");
            menu.add(0,C_Send,3,"SEND");
            menu.add(0,C_SMS,4,"SMS");
            menu.add(0,C_CALL,5,"CALL");
            menu.add(0,C_AUDIO,6,"RECORD VOICE");
            menu.add(0,C_VIDEO,7,"RECORD VIDEO");
            menu.add(0,C_SEEVIDEO,8,"SEE VIDEO");
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {


        switch (item.getItemId())
        {
            case C_View:

                Intent intent6=new Intent(ContactsApp.this,ContactDetails.class);
                AdapterView.AdapterContextMenuInfo info1 = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                int index1 = info1.position;

                intent6.putExtra("details", arrayListContact.get(index1));

                startActivity(intent6);

                break;

            case C_Delete:
                Toast.makeText(ContactsApp.this,"Delete",Toast.LENGTH_SHORT).show();

                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                int index = info.position;

                Log.e("index",index+" ");
                arrayListContact.remove(index);
                contactAdapter.notifyDataSetChanged();

                break;

            case C_Send:
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(ContactsApp.this,"Este celular no soporta el bluetooth",Toast.LENGTH_SHORT).show();
                }

                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                }

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                // If there are paired devices
                if (pairedDevices.size() > 0) {
                    // Loop through paired devices
                    for (BluetoothDevice device : pairedDevices) {
                        // Add the name and address to an array adapter to show in a ListView
                        mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    }
                }
                break;

            case C_SMS:
                AdapterView.AdapterContextMenuInfo info2 = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                int index2 = info2.position;

                String phoneNumber = arrayListContact.get(index2).getNumber();

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
                intent.putExtra("sms_body", "ESTO ES UN MENSAJE");
                startActivity(intent);

                break;

            case C_CALL:
                AdapterView.AdapterContextMenuInfo info3 = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                int index3 = info3.position;

                String phoneNumber2 = arrayListContact.get(index3).getNumber();

                Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber2, null));
                startActivity(intent1);

                break;

            case C_AUDIO:
                Intent intentV=new Intent(ContactsApp.this,AudioRecord.class);
                startActivity(intentV);
                break;

            case C_VIDEO:
                dispatchTakeVideoIntent();

                break;

            case C_SEEVIDEO:
                mVideoView.start();
                break;
        }
        return  true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            mVideoView.setVideoURI(videoUri);
        }
        if (resultCode==2) {

            contacts = (Contacts) data.getSerializableExtra("data");

            arrayListContact.add(contacts);
            contactAdapter.notifyDataSetChanged();
        }
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }
}
