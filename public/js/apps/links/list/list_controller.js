TeamReader.module('LinksApp.List', function(List, TeamReader, Backbone, Marionette, $, _) {
  List.Controller = {
    listLinks: function() {
      var loadingView = new TeamReader.Common.Views.Loading();
      TeamReader.mainRegion.show(loadingView);

      var fetchingLinks = TeamReader.request("link:entities");
      $.when(fetchingLinks).done(function(links) {
        var linksView = new List.Links({
          collection: links
        });

        linksView.on("itemview:link:delete", function(childView, model) {
          model.destroy();
        });

        linksView.on("itemview:link:show", function(childView, model) {
          TeamReader.trigger("link:show", model.get('id'));
        });

        TeamReader.mainRegion.show(linksView);
      });
    }
  }
});