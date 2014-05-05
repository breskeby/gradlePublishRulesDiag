package org.acme.demo

import org.gradle.api.Named

/**
 * Created by Rene on 05/05/14.
 */
class ProvidedInterface implements Named{
    String name

    public ProvidedInterface(String name){
        this.name = name
    }

    @Override
    String getName() {
        return name;
    }
}
