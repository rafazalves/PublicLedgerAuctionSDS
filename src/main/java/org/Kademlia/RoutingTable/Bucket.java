package org.Kademlia.RoutingTable;

import org.Kademlia.*;
import org.Kademlia.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;

public class Bucket {
    private TreeSet<Contactos> contactos;
    private final int K_Nodes; // How many nodes each bucket can hold.

    public Bucket(int k) {
        this.contactos = new TreeSet<>();
        this.K_Nodes = k;
    }

    public synchronized Contactos removeDoContactos(Node n) { // remove node
        Contactos c = ListContactos(n);
        this.contactos.remove(c);
        return c;
    }

    public synchronized Contactos ListContactos(Node n) { //return contact de um bucket
        for (Contactos c : this.contactos) {
            if (c.getN().equals(n)) {
                return c;
            }
        }
        throw new NoSuchElementException("Contacto não existe na lista");
    }

    public synchronized List<Contactos> getContactos() {
        ArrayList<Contactos> contactos = new ArrayList<>();

        if (this.contactos.isEmpty()) {
            return contactos;
        }
        for (Contactos c : this.contactos) {
            contactos.add(c);
        }
        return contactos;
    }


    public synchronized boolean addContactos(Contactos c) {
        return false;
        /*if (this.contactos.contains(c)) { // se o bucket já contém o contacto
            var aux = this.removeFromContactos(c.getN());
            aux.updateLastSeen();
            return this.contactos.add(aux);

        } else if (this.contactos.size() < K_Nodes) { // se o bucket não estiver cheio
            return this.contactos.add(c);
        } else { // se o bucket estiver cheio
            Contactos maisAntigo = contactos.pollFirst();
            // fazer ping ao contacto mais antigo e ver se é obtida resposta, se sim, discartar novo contacto e mover contacto mais antigo para o fim da lista
            //dar update ao lastSeen
            // se não for obtida resposta, remover contacto mais antigo e adicionar novo contacto
            if (ping(maisAntigo.getN().getNodeIP())) {
                var aux = this.removeFromContactos(c.getN());
                aux.updateLastSeen();
                return this.contactos.add(aux);
            } else {
                contactos.remove(maisAntigo);
                return contactos.add(c);
            }
        }

         */
    }



    public synchronized boolean addNode(Node n) {
        return this.addContactos(new Contactos(n));
    }

    public boolean isFull() {
        return contactos.size() == K_Nodes;
    }

    public boolean isEmpty() {
        return contactos.isEmpty();
    }

    public synchronized void penaltyContacto(Node n) {
        try {
            Contactos contacto = this.ListContactos(n);
            this.contactos.remove(contacto);
            // Check if the contact has exceeded the maximum retries
            if (contacto.getFailedTries() > Utils.MAX_RETRIES) {
                this.removeDoContactos(n);
            } else {
                // Increment the failed attempts and re-add the contact to the list
                contacto.setFailedTries(contacto.getFailedTries() + 1);
                this.addContactos(contacto);
            }
        } catch (NoSuchElementException e) {
            System.out.println("Contacto não encontrado");
        }
    }
}
