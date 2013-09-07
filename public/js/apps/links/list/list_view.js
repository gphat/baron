TeamReader.module('LinksApp.List', function(List, TeamReader, Backbone, Marionette, $, _) {
  List.Link = Marionette.ItemView.extend({
    tagName: "tr",
    template: "#link-template",
    events: {
      "click a.js-show": "showClicked",
      "click button.js-delete": "deleteClicked"
    },
    deleteClicked: function(e) {
      e.stopPropagation();
      this.trigger("link:delete", this.model);
    },
    showClicked: function(e) {
      e.preventDefault();
      e.stopPropagation();
      this.trigger("link:show", this.model);
    },
    remove: function() {
      this.$el.fadeOut(function() {
        $(this).remove();
      });
    }
  });

  List.Links = Marionette.CompositeView.extend({
    tagName: "table",
    className: "table table-hover",
    template: "#link-list",
    itemView: List.Link,
    itemViewContainer: "tbody"
  })
});