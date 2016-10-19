package com.traversoft.gdgphotoshare.data;


import java.lang.reflect.Array;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * Clutch for Android
 * Copyright © 2016 HFA. All rights reserved.
 */

@AllArgsConstructor
public class Speaker {

    @Getter @Setter private int id;
    @Getter @Setter private boolean featured;
    @Getter @Setter private String name;
    @Getter @Setter private String company;
    @Getter @Setter private String bio;
    @Getter @Setter private String photoUrl;
    @Getter @Setter private String country;
    @Getter @Setter private String title;

    @Getter @Setter private ArrayList<Socials> socials;
    @Getter @Setter private ArrayList<String> tags;

    public Speaker() {
    }
}
