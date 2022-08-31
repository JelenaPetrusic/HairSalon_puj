module com.jlm.hairsalon {

    requires javafx.controls;
    requires javafx.fxml;
    //requires javafx.web;
    requires java.sql;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    //requires validatorfx;
    //requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.jetbrains.annotations;
    //requires eu.hansolo.tilesfx;

    opens com.jlm.hairsalon to javafx.fxml;
    exports com.jlm.hairsalon;
    exports com.jlm.hairsalon.controller;
    opens com.jlm.hairsalon.controller to javafx.fxml;
    opens com.jlm.hairsalon.model to javafx.base;


}