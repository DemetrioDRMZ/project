package project;

import net.sf.clipsrules.jni.*;

class BassicDemo {

    Environment clips;

    public BassicDemo(){
        clips = new Environment();
    }

    public void inicializarEI(){
        clips.eval("(reset)");
    }

    public void cargarHecho(String fact){
        clips.eval("(assert"+ fact + ")");
    }

    public void cargarRegla(String rule){
        clips.build("(defrule " + rule + ")");
    }

    public void ejecutarReglas(){
        clips.run();
    }

    public void mostrarHechos(){
        clips.eval("(facts)");
    }

    public void mostrarReglas(){
        clips.eval("(rules)");
    }

    public void cargarPlantillas(String Ruta){
        clips.load(Ruta);
    }

    public void limpiarEI(){
        clips.eval("(clear)");
    }

    public void cargarProducto(String nombre, String number, String categoria, String price){
        clips.eval("(assert (product (part-number "+ number +") (name "+ nombre +") (category "+ categoria +") (price "+ price +")))");
    }

    public void cargarOrder(String order_number, String customer_id){
        clips.eval("(assert (order (order-number "+ order_number +") (customer-id "+ customer_id +")))");
    }

    public void cargarLineItem(String order_number, String number, String customer_id, String cantidad){
        clips.eval("(assert (line-item (order-number "+ order_number +") (part-number "+ number +") (customer-id "+ customer_id +") (quantity "+ cantidad +")))");
    }
}