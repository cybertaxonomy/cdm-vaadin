
/**
 * D3 Layout Tree (inspired by http://bl.ocks.org/mbostock/4339083)
 * which displays a list concept relationships between a 'from' taxon 
 * and a number of 'to' taxon
 */
window.eu_etaxonomy_cdm_vaadin_jscomponent_D3ConceptRelationshipTree = function() {
    var connector = this;
    var diagramElement = connector.getElement();
    var margin = {top: 20, right: 120, bottom: 20, left: 120},
    width = 740 - margin.right - margin.left,
    height = 600 - margin.top - margin.bottom;

    var i = 0,
    duration = 750,
    root;

    var tree;
    var diagonal;

    var svg = d3.select(diagramElement).append("svg")
    .attr("width", width + margin.right + margin.left)
    .attr("height", height + margin.top + margin.bottom)
    .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    var selectedNode;
    
    var orientations = {
            "topbottom": {
                size: [width, height],
                x: function(d) { return d.x; },
                y: function(d) { return d.y; }
            },
            "rightleft": {
                size: [height, width],
                x: function(d) { return width - d.y; },
                y: function(d) { return d.x; },
                xend: function(d) { return d.y; },
            },
            "bottomtop": {
                size: [width, height],
                x: function(d) { return d.x; },
                y: function(d) { return height - d.y; }
            },
            "leftright": {
                size: [height, width],
                x: function(d) { return d.y; },
                y: function(d) { return d.x; },
                xend: function(d) { return width - d.y; },
            }
    };
    // default setting is left-right
    var orientation = orientations.rightleft;    
    var tAnchorWithChildren = "end";
    var tAnchorWithoutChildren = "start";
    var dirMult = 1;
    this.onStateChange = function() {
        crTree = this.getState().conceptRelationshipTree;
       
        if(crTree) {
            root = JSON.parse(connector.getState().conceptRelationshipTree);
                        
            if(root.direction === "left-right") {               
                orientation = orientations.leftright;       
                tAnchorWithChildren = "end";
                tAnchorWithoutChildren = "start";
                dirMult = 1;
            }
            if(root.direction === "right-left") {                
                orientation = orientations.rightleft;      
                tAnchorWithChildren = "start";
                tAnchorWithoutChildren = "end";
                dirMult = -1;
            }
                        
            diagonal = d3.svg.diagonal().projection(function(d) { return [orientation.x(d), orientation.y(d)]; });                
            tree = d3.layout.tree().size(orientation.size);
            update(root);
            d3.select(self.frameElement).style("height", "800px");
        }       
    }

    
    function update(source) {

        // Compute the new tree layout.
        var nodes = tree.nodes(root).reverse(),
        links = tree.links(nodes);

        // Normalize for fixed-depth.
        nodes.forEach(function(d) { d.y = d.depth * 180; });

        // Update the nodes…
        var node = svg.selectAll("g.node")
        .data(nodes, function(d) { return d.id || (d.id = ++i); });


        // Enter any new nodes at the parent's previous position.
        var nodeEnter = node.enter().append("g")
        .attr("class", "node")
        .attr("transform", function(d) { return "translate(" + orientation.x(source) + "," + orientation.y(source) + ")"; })
        .on("click", click);

        nodeEnter.append("circle")
        .attr("r", function(d) { return d.type === "taxon" ? 5 : d.type === "conceptr" ? 10 : 0; })
        .style("fill", function(d) { return d === source && d.type === "conceptr" ? "#DF7401" : "#fff"; });
        
        nodeEnter.append("text")
        .attr("x", function(d) { 
            if(d.type === "conceptr") { 
                return dirMult*50;
            } else {
                return d.children || d._children ? -1*dirMult*10 : dirMult*10; 
            }
        })
        .attr("y", function(d) { return d.type === "conceptr" ? -20 : 0; })
        .attr("dy", ".35em")
        .attr("text-anchor", function(d) { return d.children || d._children ? tAnchorWithChildren : tAnchorWithoutChildren; })
        .text(function(d) { return d.name; })
        .style("fill-opacity", 1e-6);

        // Transition nodes to their new position.
        var nodeUpdate = node.transition()
        .duration(duration)
        .attr("transform", function(d) { return "translate(" + orientation.x(d) + "," + orientation.y(d) + ")"; });

        nodeUpdate.select("circle")
        .attr("r", function(d) { return d.type === "taxon" ? 5 : d.type === "conceptr" ? 10 : 0; })
        .style("fill", function(d) { return d === selectedNode && d.type === "conceptr" ? "#DF7401" : "#fff"; });

        nodeUpdate.select("text")
        .style("fill-opacity", 1);

        // Transition exiting nodes to the parent's new position.
        var nodeExit = node.exit().transition()
            .duration(duration)
            .attr("transform", function(d) { return "translate(" + orientation.xend(source) + "," + orientation.y(source) + ")"; })
            .remove();

        nodeExit.select("circle")
            .attr("r", 1e-6);

        nodeExit.select("text")
            .style("fill-opacity", 1e-6);
        
        // Update the links…
        var link = svg.selectAll("path.link")
        .data(links, function(d) { return d.target.id; });

        // Remove links without transition
        link.exit().remove();
        
        // Enter any new links at the parent's previous position.
        link.enter().insert("path", "g")
        .attr("class", "link")
        .attr("d", function(d) {
            var o = {x: source.x, y: source.y};
            return diagonal({source: o, target: o});
        });

        // Transition links to their new position.
        link.transition()
        .duration(duration)
        .attr("d", diagonal);


    }

//  Toggle children on click.
    function click(d) {
        //root.children.forEach(collapse);

        if(d.type === "conceptr") {
            connector.select(d.uuid);
        }

        selectedNode = d;
        update(d);
    }
}