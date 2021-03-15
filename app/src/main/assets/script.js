document.addEventListener('DOMContentLoaded', (event) => {

    var attempts = 0;
    var limit = 5;
    var checkLength=2;

    try {
        var expected = CHIVE_GALLERY_ITEMS.items.length;
        if (expected > checkLength){
            checkLength = expected;
        }
        console.info("Chiver is expecting " + checkLength + " items");
    } catch (e) {}

    var parse = function() {
        console.info("Chiver is parsing :)");

        var nodes = document.querySelectorAll('div.gallery-icon > img,div.gallery-icon > video > source');

        if (nodes.length < checkLength && attempts < limit) {
            attempts++;
            setTimeout(parse, 500);
            return;
        }

        for(var i=0;i<nodes.length; i++){
            var a = nodes[i];
            var url = a.getAttribute("src");
            if (url != null) {

                url = url.split("?")[0];

                if (url.toLowerCase().endsWith(".mp4")) {
                    url = url.replace(/^(.+)\.mp4$/i, "$1.gif")
                }

                wai.addGalleryItem(url);
            }
        }
        wai.notifyDataSetChanged();
   }

    setTimeout(function(){
        parse();
    }, 500);
});
