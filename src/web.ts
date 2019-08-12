import {WebPlugin} from '@capacitor/core';
import {ContactsPlugin} from './definitions';

export class ContactsWeb extends WebPlugin implements ContactsPlugin {
    constructor() {
        super({
            name: 'Contacts',
            platforms: ['web']
        });
    }

    async getContacts(): Promise<{ contacts: Array<any> }> {
        console.log('GET_CONTACTS', 'called');
        return {contacts: []};
    }

    async getGroups(): Promise<{ groups: Array<any> }> {
        console.log('GET_GROUPS', 'called');
        return {groups: []};
    }

    async getContactGroups(): Promise<any> {
        console.log('GET_CONTACT_GROUPS', 'called');
        return {};
    }

    async deleteContact(options: { lookupKey: string }): Promise<any> {
        console.log('DELETE_CONTACT', options);
        return {};
    }

}

const Contacts = new ContactsWeb();

export {Contacts};

import {registerWebPlugin} from '@capacitor/core';

registerWebPlugin(Contacts);
