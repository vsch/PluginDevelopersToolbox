# Plugin Developer's Toolbox

A collection of useful functions for plugin developers. It is something I plan on expanding as I get to implement features that I find useful in development of the idea-multimarkdown plugin. 

[Version Notes]

## Version 1.0.0 Initial Release

For now it is just one extremely useful function if you use [Slicy] for extracting icon images from PSD files. 

### Slicy File Post-Processor

[Slicy] from [macrabbit] extracts individual icons from PSD files containing layer groups with specific names. It will also create @1x icons from @2x icons for file names that end in @2x.ext and will put extracted icons in subdirectories if the parent layer group ends in /. 

Why bother? If you maintain a plugin, then you will need to create 4x icons for every icon used in the IDEA: @1x light, @1x dark, @2x light, @2x dark. Each with a proper naming convention, so the IDEA recognizes them as retina and scheme type. 
                  
Slicy is an awesome tool that will let you create a single PSD file with multiple icon images and monitor the file to automatically export the icons when the file changes. It will also generate @1x images from @2x images if a @1x image was not provided in the file. This cuts the number of images to maintain in half. 

The plugin augments this in the following way:

1. The slicy naming convention does not match IDEA for darcula @2x icons. So the plugin will rename any newly created image files matching `fileName_dark@2x.ext` to `fileName_dark@2x.ext`, where ext is `.png`, `.jpg`, `.jpeg` or `.gif`.

2. For each icon you need to create 2 layer groups: `fileName@2x.ext` and `fileName_dark@2x.ext`. This may not seem like much but when you add a new icon you need to copy and rename 2 layer groups, and make sure you match the naming convention for each layer. This plugin will let you just create a template of layer groups to be reused for each icon:

        iconName.+/
            +@2x.png
                icon layers go here
            _dark@2x.png
                icon layers go here

    To create a new icon, you only have to duplicate the iconName.+/ group and rename it to newIconName.+/ group. When you save the file Slicy will do its thing and create the following files, in whatever directory you originally told it to save the icons:     
    
        directory/
            iconName.+/
                +@2x.png
                +.png
                _dark@2x.png
                _dark.png
            newIconName.+/
                +@2x.png
                +.png
                _dark@2x.png
                _dark.png

    Now the plugin will come into action and transpose the above into what the IDEA will recognize as a set of 2 icons with normal, retina, default and darcula scheme variations: 
    
        directory/
            iconName@2x.png
            iconName.png
            iconName@2x_dark.png
            iconName_dark.png
            newIconName@2x.png
            newIconName.png
            newIconName@2x_dark.png
            newIconName_dark.png
  
    
[Slicy]: http://www.macrabbit.com/slicy    
[macrabbit]: http://www.macrabbit.com
[Version Notes]: /resources/META-INF/VERSION.md
    

