package com.traversoft.gdgphotoshare.data;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * Clutch for Android
 * Copyright © 2016 HFA. All rights reserved.
 */

@AllArgsConstructor
public class Socials {
    @Getter @Setter private String icon;
    @Getter @Setter private String link;
    @Getter @Setter private String name;

    public Socials() {
    }
}
