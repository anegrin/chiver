var tcparse = function() {
    setTimeout(function(){
        console.info("Chiver is parsing :)");
        var nodes = document.querySelectorAll('div.gallery-icon > img,div.gallery-icon > video > source');
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
    }, 500);
}
