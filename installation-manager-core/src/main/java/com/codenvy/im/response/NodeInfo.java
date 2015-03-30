/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2015] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.im.response;

import com.codenvy.im.node.NodeConfig;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** @author Dmytro Nochevnov */
@JsonPropertyOrder({"type", "host", "status"})
public class NodeInfo {
    private NodeConfig.NodeType type;
    private String host;
    private Status status;

    public NodeInfo() {
    }

    public NodeInfo(NodeConfig.NodeType type, String host, Status status) {
        this.type = type;
        this.host = host;
        this.status = status;
    }

    public NodeConfig.NodeType getType() {
        return type;
    }

    public NodeInfo setType(NodeConfig.NodeType type) {
        this.type = type;
        return this;
    }

    public String getHost() {
        return host;
    }

    public NodeInfo setHost(String host) {
        this.host = host;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public NodeInfo setStatus(Status status) {
        this.status = status;
        return this;
    }

    public static NodeInfo createSuccessInfo(NodeConfig node) {
        return new NodeInfo(node.getType(), node.getHost(), Status.SUCCESS);
    }
}