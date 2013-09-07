TeamReader.module("LinksApp", function(LinksApp, TeamReader, Backbone, Marionette, $, _) {
  LinksApp.Router = Marionette.AppRouter.extend({
    appRoutes: {
      "links": "listLinks",
      "links/:id": "showLink"
    }
  });

  var API = {
    listLinks: function() {
      LinksApp.List.Controller.listLinks();
    },
    showLink: function(id) {
      LinksApp.Show.Controller.showLink(id);
    }
  };

  TeamReader.on("links:list", function() {
    TeamReader.navigate("links");
    API.listLinks();
  });

  TeamReader.on("link:show", function(id) {
    TeamReader.navigate("links/" + id);
    API.showLink(id);
  });

  TeamReader.addInitializer(function() {
    new LinksApp.Router({
      controller: API
    });
  });
});
