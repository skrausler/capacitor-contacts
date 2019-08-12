package at.loveit.capacitorcontacts;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import org.json.JSONException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NativePlugin(permissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS})
public class Contacts extends Plugin {

    static final int REQUEST_CONTACTS_PERMISSIONS = 12345;

    public static final String CONTACT_ID = "contactId";
    public static final String EMAILS = "emails";
    public static final String PHONE_NUMBERS = "phoneNumbers";
    public static final String LOOKUP_KEY = "lookupKey";

    @PluginMethod()
    public void getContacts(PluginCall call) {

        requestPermissions();

        JSObject result = new JSObject();
        JSArray jsContacts = new JSArray();
        Cursor dataCursor = getContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null);
        while (dataCursor.moveToNext()) {
            JSObject jsContact = new JSObject();
            String contactId = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Contacts._ID));
            jsContact.put(CONTACT_ID, contactId);
            jsContact.put(LOOKUP_KEY, dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)));
            jsContact.put("displayName", dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)));

            addPhoneNumbers(jsContact);
            addEmails(jsContact);
            jsContacts.put(jsContact);
        }
        dataCursor.close();

        result.put("contacts", jsContacts);
        call.success(result);
    }

    @PluginMethod()
    public void getGroups(PluginCall call) {

        requestPermissions();

        JSObject result = new JSObject();
        JSArray jsGroups = new JSArray();
        Cursor dataCursor = getContext().getContentResolver().query(ContactsContract.Groups.CONTENT_URI,
                null,
                null,
                null,
                null);

        while (dataCursor.moveToNext()) {
            JSObject jsGroup = new JSObject();
            String groupId = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Groups._ID));
            jsGroup.put("groupId", groupId);
            jsGroup.put("accountType", dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Groups.ACCOUNT_TYPE)));
            jsGroup.put("accountName", dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Groups.ACCOUNT_NAME)));
            jsGroup.put("title", dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Groups.TITLE)));
            jsGroups.put(jsGroup);
        }
        dataCursor.close();

        result.put("groups", jsGroups);
        call.success(result);
    }

    @PluginMethod()
    public void getContactGroups(PluginCall call) {

        requestPermissions();

        Cursor dataCursor = getContext().getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data.CONTACT_ID,
                        ContactsContract.Data.DATA1},
                ContactsContract.Data.MIMETYPE + "=?", new String[]{ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE}, null);

        Map<String, Set<String>> contact2GroupMap = new HashMap<>();
        while (dataCursor.moveToNext()) {
            String contact_id = dataCursor.getString(0);
            String group_id = dataCursor.getString(1);

            Set<String> groups = new HashSet<>();
            if (contact2GroupMap.containsKey(contact_id)) {
                groups = contact2GroupMap.get(contact_id);
            }
            groups.add(group_id);
            contact2GroupMap.put(contact_id, groups);
        }
        dataCursor.close();

        JSObject result = new JSObject();
        for (Map.Entry<String, Set<String>> entry : contact2GroupMap.entrySet()) {
            JSArray jsGroups = new JSArray();
            Set<String> groups = entry.getValue();
            for (String group : groups) {
                jsGroups.put(group);
            }
            result.put(entry.getKey(), jsGroups);
        }

        call.success(result);
    }

    private void requestPermissions() {
        pluginRequestPermissions(new String[]{
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
        }, REQUEST_CONTACTS_PERMISSIONS);
    }

    private void addEmails(JSObject jsContact) {
        try {
            jsContact.put(EMAILS, new JSArray());
            String contactId = (String) jsContact.get(CONTACT_ID);
            Cursor cur1 = getContext().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    new String[]{contactId}, null);
            while (cur1.moveToNext()) {
                String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                JSArray emails = (JSArray) jsContact.get(EMAILS);
                emails.put(email);
            }
            cur1.close();
        } catch (JSONException e) {
            Log.e("Contacts", "JSONException addEmails");
        }
    }

    private void addPhoneNumbers(JSObject jsContact) {
        try {
            jsContact.put(PHONE_NUMBERS, new JSArray());
            String contactId = (String) jsContact.get(CONTACT_ID);
            Cursor cur1 = getContext().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{contactId}, null);
            while (cur1.moveToNext()) {
                String phoneNumber = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                JSArray phoneNumbers = (JSArray) jsContact.get(PHONE_NUMBERS);
                phoneNumbers.put(phoneNumber);
            }
            cur1.close();
        } catch (JSONException e) {
            Log.e("Contacts", "JSONException addPhoneNumbers");
        }
    }

    @PluginMethod()
    public void deleteContact(PluginCall call) {

        requestPermissions();

        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, call.getString(LOOKUP_KEY));
        getContext().getContentResolver().delete(uri, null, null);

        JSObject result = new JSObject();
        call.success(result);
    }

}
