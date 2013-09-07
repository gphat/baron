TeamReader.module("LinksApp.Show", function(Show, TeamReader, Backbone, Marionette, $, _) {
  Show.Controller = {
    showLink: function(id) {
      var loadingView = new TeamReader.Common.Views.Loading();
      TeamReader.mainRegion.show(loadingView);

      var fetchingLink = TeamReader.request("link:entity", id);
      $.when(fetchingLink).done(function(link) {
        var linkView;
        if(link !== undefined) {
          linkView = new Show.Link({
            model: link
          });
        } else {
          linkView = new Show.MissingLink();
        }
        TeamReader.mainRegion.show(linkView);
      });
    }
  }
});