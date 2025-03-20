package com.github.philipepompeu.cloud_product_catalog.application.dto;

public class JsonViews {

    public static class Default {}  // Para endpoints normais (ex: /category)
    public static class Catalog extends Default {}  // Para o catálogo
}
