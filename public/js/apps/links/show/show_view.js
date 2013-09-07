TeamReader.module("LinksApp.Show", function(Show, TeamReader, Backbone, Marionette, $, _) {
  Show.MissingLink = Marionette.ItemView.extend({
    template: "#missing-link-view"
  });

  Show.Link = Marionette.ItemView.extend({
    template: "#link-view"
  });
});