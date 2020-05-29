module in.co.dermatologist.dicoderma {
    requires lombok;
    requires java.desktop;
    requires org.apache.commons.imaging;
    requires com.google.gson;
    requires static dcm4che.tool.common;
    requires static dcm4che.core;
    requires static dcm4che.tool.jpg2dcm;
    requires static commons.cli;
    requires static dcm4che.imageio;
    requires com.fasterxml.jackson.dataformat.javaprop;
    requires com.fasterxml.jackson.annotation;

    exports in.co.dermatologist.dicoderma;
}