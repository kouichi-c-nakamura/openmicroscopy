{% comment %}
<!--
  Copyright (C) 2012 University of Dundee & Open Microscopy Environment.
  All rights reserved.

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as
  published by the Free Software Foundation, either version 3 of the
  License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Affero General Public License for more details.

  You should have received a copy of the GNU Affero General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->
{% endcomment %}

<script>

$(document).ready(function() {

    // this script is an 'include' within a django for-loop, so we can get our index:
    var overlay_plugin_index = {{ forloop.counter }};

    var update_channel_overlay = function() {
        
        // this may have been called before datatree was initialised...
        var datatree = $.jstree._focused();
        if (!datatree) return;
        
        // get the selected id etc
        var selected = datatree.data.ui.selected;
        var orel = selected.attr('rel').replace("-locked", "");
        var oid = selected.attr('id');
        if (orel != "image") return;
        
        // if the tab is visible and not loaded yet...
        $channel_overlay_panel = $("#channel_overlay_panel");
        var channel_overlay_url;
        if ($channel_overlay_panel.is(":visible") && $channel_overlay_panel.is(":empty")) {
            split_view_fig_url = '{% url webtest_index %}channel_overlay_viewer/'+ oid.split("-")[1] + "/";
            $channel_overlay_panel.load(split_view_fig_url);
        };
    };
    
    // update tabs when tree selection changes or tabs switch
    $('#center_panel_chooser').bind('change', update_channel_overlay);

    // on change of selection in tree, update which tabs are enabled
    $("#dataTree").bind("select_node.jstree", function(e, data) {

        // clear contents of plugin
        $("#channel_overlay_panel").empty();

        var selected = data.inst.get_selected();
        var orel = selected.attr('rel').replace("-locked", "");

        // update enabled state
        if((orel!="image") || (selected.length > 1)) {
            set_center_plugin_enabled(overlay_plugin_index, false);
        } else {
            set_center_plugin_enabled(overlay_plugin_index, true);
        }

        // update tab content
        update_channel_overlay();
    });
    
    // update after we've loaded the document
    //update_img_viewer();
});

</script>