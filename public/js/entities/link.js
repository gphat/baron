TeamReader.module('Entities', function(Entities, TeamReader, Backbone, Marionette, $, _) {

  Entities.Link = Backbone.Model.extend({
    urlRoot: "/api/links",
    defaults: {
      description: "",
      url: ""
    }
  });

  Entities.LinkCollection = Backbone.Collection.extend({
    url: "/api/links",
    model: Entities.Link,
    comparator: "description"
  });

  var API = {
    getLinkEntities: function() {
      var links = new Entities.LinkCollection();
      var defer = $.Deferred();
      setTimeout(function() {
        links.fetch({
          success: function(data) {
            defer.resolve(data);
          },
          error: function(data) {
            defer.resolve(undefined);
          }
        });
      }, 2000);
      return defer.promise();
    },
    getLinkEntity: function(linkId) {
      var link = new Entities.Link({ id: linkId });
      var defer = $.Deferred();
      setTimeout(function() {
        link.fetch({
          success: function(data) {
            defer.resolve(data);
          },
          error: function(data) {
            defer.resolve(undefined);
          }
        });
      }, 2000);
      return defer.promise();
    }
  }

  TeamReader.reqres.setHandler("link:entities", function() {
    return API.getLinkEntities();
  });

  TeamReader.reqres.setHandler("link:entity", function(id) {
    return API.getLinkEntity(id);
  });
});