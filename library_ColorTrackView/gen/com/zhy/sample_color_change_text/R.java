/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * aapt tool from the resource data it found.  It
 * should not be modified by hand.
 */

package com.zhy.sample_color_change_text;

public final class R {
    public static final class attr {
        /** <p>Must be one of the following constant values.</p>
<table>
<colgroup align="left" />
<colgroup align="left" />
<colgroup align="left" />
<tr><th>Constant</th><th>Value</th><th>Description</th></tr>
<tr><td><code>left</code></td><td>0</td><td></td></tr>
<tr><td><code>right</code></td><td>1</td><td></td></tr>
</table>
         */
        public static int direction=0x7f010005;
        /** <p>Must be a floating point value, such as "<code>1.2</code>".
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
         */
        public static int progress=0x7f010004;
        /** <p>Must be a string value, using '\\;' to escape characters such as '\\n' or '\\uxxxx' for a unicode character.
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
         */
        public static int text=0x7f010000;
        /** <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
         */
        public static int text_change_color=0x7f010003;
        /** <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
         */
        public static int text_origin_color=0x7f010002;
        /** <p>Must be a dimension value, which is a floating point number appended with a unit such as "<code>14.5sp</code>".
Available units are: px (pixels), dp (density-independent pixels), sp (scaled pixels based on preferred font size),
in (inches), mm (millimeters).
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
         */
        public static int text_size=0x7f010001;
    }
    public static final class dimen {
        /**  Default screen margins, per the Android Design guidelines. 

         Customize dimensions originally defined in res/values/dimens.xml (such as
         screen margins) for sw720dp devices (e.g. 10" tablets) in landscape here.
    
         */
        public static int activity_horizontal_margin=0x7f040000;
        public static int activity_vertical_margin=0x7f040001;
    }
    public static final class drawable {
        public static int ic_launcher=0x7f020000;
    }
    public static final class id {
        public static int action_settings=0x7f030002;
        public static int left=0x7f030000;
        public static int right=0x7f030001;
    }
    public static final class menu {
        public static int main=0x7f070000;
    }
    public static final class string {
        public static int action_settings=0x7f050001;
        public static int app_name=0x7f050000;
        public static int hello_world=0x7f050002;
    }
    public static final class style {
        /** 
        Base application theme, dependent on API level. This theme is replaced
        by AppBaseTheme from res/values-vXX/styles.xml on newer devices.
    

            Theme customizations available in newer API levels can go in
            res/values-vXX/styles.xml, while customizations related to
            backward-compatibility can go here.
        

        Base application theme for API 11+. This theme completely replaces
        AppBaseTheme from res/values/styles.xml on API 11+ devices.
    
 API 11 theme customizations can go here. 

        Base application theme for API 14+. This theme completely replaces
        AppBaseTheme from BOTH res/values/styles.xml and
        res/values-v11/styles.xml on API 14+ devices.
    
 API 14 theme customizations can go here. 
         */
        public static int AppBaseTheme=0x7f060000;
        /**  Application theme. 
 All customizations that are NOT specific to a particular API-level can go here. 
         */
        public static int AppTheme=0x7f060001;
    }
    public static final class styleable {
        /** Attributes that can be used with a ColorTrackView.
           <p>Includes the following attributes:</p>
           <table>
           <colgroup align="left" />
           <colgroup align="left" />
           <tr><th>Attribute</th><th>Description</th></tr>
           <tr><td><code>{@link #ColorTrackView_direction com.zhy.sample_color_change_text:direction}</code></td><td></td></tr>
           <tr><td><code>{@link #ColorTrackView_progress com.zhy.sample_color_change_text:progress}</code></td><td></td></tr>
           <tr><td><code>{@link #ColorTrackView_text com.zhy.sample_color_change_text:text}</code></td><td></td></tr>
           <tr><td><code>{@link #ColorTrackView_text_change_color com.zhy.sample_color_change_text:text_change_color}</code></td><td></td></tr>
           <tr><td><code>{@link #ColorTrackView_text_origin_color com.zhy.sample_color_change_text:text_origin_color}</code></td><td></td></tr>
           <tr><td><code>{@link #ColorTrackView_text_size com.zhy.sample_color_change_text:text_size}</code></td><td></td></tr>
           </table>
           @see #ColorTrackView_direction
           @see #ColorTrackView_progress
           @see #ColorTrackView_text
           @see #ColorTrackView_text_change_color
           @see #ColorTrackView_text_origin_color
           @see #ColorTrackView_text_size
         */
        public static final int[] ColorTrackView = {
            0x7f010000, 0x7f010001, 0x7f010002, 0x7f010003,
            0x7f010004, 0x7f010005
        };
        /**
          <p>This symbol is the offset where the {@link com.zhy.sample_color_change_text.R.attr#direction}
          attribute's value can be found in the {@link #ColorTrackView} array.


          <p>Must be one of the following constant values.</p>
<table>
<colgroup align="left" />
<colgroup align="left" />
<colgroup align="left" />
<tr><th>Constant</th><th>Value</th><th>Description</th></tr>
<tr><td><code>left</code></td><td>0</td><td></td></tr>
<tr><td><code>right</code></td><td>1</td><td></td></tr>
</table>
          @attr name android:direction
        */
        public static final int ColorTrackView_direction = 5;
        /**
          <p>This symbol is the offset where the {@link com.zhy.sample_color_change_text.R.attr#progress}
          attribute's value can be found in the {@link #ColorTrackView} array.


          <p>Must be a floating point value, such as "<code>1.2</code>".
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
          @attr name android:progress
        */
        public static final int ColorTrackView_progress = 4;
        /**
          <p>This symbol is the offset where the {@link com.zhy.sample_color_change_text.R.attr#text}
          attribute's value can be found in the {@link #ColorTrackView} array.


          <p>Must be a string value, using '\\;' to escape characters such as '\\n' or '\\uxxxx' for a unicode character.
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
          @attr name android:text
        */
        public static final int ColorTrackView_text = 0;
        /**
          <p>This symbol is the offset where the {@link com.zhy.sample_color_change_text.R.attr#text_change_color}
          attribute's value can be found in the {@link #ColorTrackView} array.


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
          @attr name android:text_change_color
        */
        public static final int ColorTrackView_text_change_color = 3;
        /**
          <p>This symbol is the offset where the {@link com.zhy.sample_color_change_text.R.attr#text_origin_color}
          attribute's value can be found in the {@link #ColorTrackView} array.


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
          @attr name android:text_origin_color
        */
        public static final int ColorTrackView_text_origin_color = 2;
        /**
          <p>This symbol is the offset where the {@link com.zhy.sample_color_change_text.R.attr#text_size}
          attribute's value can be found in the {@link #ColorTrackView} array.


          <p>Must be a dimension value, which is a floating point number appended with a unit such as "<code>14.5sp</code>".
Available units are: px (pixels), dp (density-independent pixels), sp (scaled pixels based on preferred font size),
in (inches), mm (millimeters).
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
          @attr name android:text_size
        */
        public static final int ColorTrackView_text_size = 1;
    };
}
