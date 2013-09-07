var TeamReader = new Marionette.Application();

TeamReader.addRegions({
  mainRegion: "#main-region"
});

TeamReader.navigate = function(route, options) {
  options || (options = {});
  Backbone.history.navigate(route, options);
}

TeamReader.getCurrentRoute = function() {
  return Backbone.history.fragment
};

TeamReader.on("initialize:after", function() {
  if(Backbone.history) {
    Backbone.history.start();

    if(this.getCurrentRoute() === "") {
      TeamReader.trigger("links:list");
    }
  }
});
