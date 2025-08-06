package com.windowsxp.fsv.fcyt;

// ¡Fíjate que ya no necesitamos imports de JavaFX aquí!
public class Materia {
    private int numero;
    private String nivel;
    private String codigo;
    private String materia;
    private String estado;
    private String dificultad;
    private String descripcion;

    // Constructor vacío. Gson y otras librerías a veces lo necesitan.
    public Materia() {
    }

    public Materia(int numero, String nivel, String codigo, String materia, String estado, String dificultad, String descripcion) {
        this.numero = numero;
        this.nivel = nivel;
        this.codigo = codigo;
        this.materia = materia;
        this.estado = estado;
        this.dificultad = dificultad;
        this.descripcion = descripcion;
    }

    public int getNumero() {
        return numero; }
    public void setNumero(int numero) {
        this.numero = numero; }

    public String getNivel() {
        return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getCodigo()
    { return codigo; }
    public void setCodigo(String codigo) {
        this.codigo = codigo; }

    public String getMateria() {
        return materia; }
    public void setMateria(String materia) {
        this.materia = materia; }

    public String getEstado() {
        return estado; }
    public void setEstado(String estado) {
        this.estado = estado; }

    public String getDificultad() {
        return dificultad; }
    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public String getDescripcion(){
        return descripcion;
    }
    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }
}