/*
 * The MIT License
 * 
 * Copyright (C) 2014, 2015 Dominique Brice
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package jenkins.plugins.linkedjobs.model;

import hudson.model.Label;
import hudson.model.Node;
import hudson.slaves.Cloud;

import java.util.ArrayList;
import java.util.List;

import jenkins.model.Jenkins;

/**
 * Data structure to group together jobs (projects) sharing the same label
 * @author dominiquebrice
 */
public class JobsGroup extends AbstractJobsGroup implements Comparable<JobsGroup> {

    // the label shared by all jobs in this group
    private final Label label;
    // nodes that could run all jobs listed here considering their label
    private List<Node> applicableNodes;

    public JobsGroup(Label l) {
        super();

        label = l;
        
        applicableNodes = new ArrayList<Node>();
        // list all nodes that could run jobs with this particular label
        // this code is strongly inspired from what is found in hudson.model.Label.getNodes()
        Jenkins jenkins = Jenkins.getInstance();
        // do not forget master node!
        if (label.matches(jenkins)) {
            applicableNodes.add(jenkins);
        }
        for (Node node : jenkins.getNodes()) {
            if (label.matches(node)) {
                applicableNodes.add(node);
            }
        }
    }
    
    /************************************
     * functions used to render display in index.jelly
     ************************************/
    
    public String getLabel() {
        return label.getDisplayName();
    }
    
    public String getLabelURL() {
        return label.getUrl();
    }
    
    public List<Node> getNodes() {
        return applicableNodes;
    }
    
    public boolean isSingleNode() {
        return applicableNodes.size() == 1;
    }
    
    // clouds that can provision this label (and thus jobs in this group)
    public List<Cloud> getProvisioningClouds() {
        List<Cloud> provisioningClouds = new ArrayList<Cloud>();
        for (Cloud c : Jenkins.getInstance().clouds) {
            if (c.canProvision(label)) {
                provisioningClouds.add(c);
            }
        }
        return provisioningClouds;
    }

    /************************************
     * implements Comparable<JobsGroup>
     ************************************/
    public int compareTo(JobsGroup o) {
        return this.label.compareTo(o.label);
    }
}