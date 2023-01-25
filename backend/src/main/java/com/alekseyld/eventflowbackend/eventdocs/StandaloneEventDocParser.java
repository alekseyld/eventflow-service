package com.alekseyld.eventflowbackend.eventdocs;

import com.alekseyld.eventflowbackend.eventdocs.domain.service.EventDocService;

public class StandaloneEventDocParser {

    public static void main(String[] args) {

        new EventDocService().processDocFile();
    }
}
