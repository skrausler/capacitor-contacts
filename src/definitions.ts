declare module "@capacitor/core" {
    interface PluginRegistry {
        Contacts: ContactsPlugin;
    }
}

export interface ContactsPlugin {
    requestPermissions(): Promise<any>;
    getContacts(): Promise<{ contacts: Array<any> }>;
    getGroups(): Promise<{ groups: Array<any> }>;
    getContactGroups(): Promise<any>;
    deleteContact(options: { lookupKey: string }): Promise<any>;
}
