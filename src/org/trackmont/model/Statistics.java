/*
 * Copyright 2016 - 2017 Vicente Venegas (vicente@republik.ec)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trackmont.model;

import java.util.Date;

public class Statistics extends ExtendedModel {

    private Date captureTime;

    public Date getCaptureTime() {
        if (captureTime != null) {
            return new Date(captureTime.getTime());
        } else {
            return null;
        }
    }

    public void setCaptureTime(Date captureTime) {
        if (captureTime != null) {
            this.captureTime = new Date(captureTime.getTime());
        } else {
            this.captureTime = null;
        }
    }

    private int activeUsers;

    public int getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(int activeUsers) {
        this.activeUsers = activeUsers;
    }

    private int activeDevices;

    public int getActiveDevices() {
        return activeDevices;
    }

    public void setActiveDevices(int activeDevices) {
        this.activeDevices = activeDevices;
    }

    private int requests;

    public int getRequests() {
        return requests;
    }

    public void setRequests(int requests) {
        this.requests = requests;
    }

    private int messagesReceived;

    public int getMessagesReceived() {
        return messagesReceived;
    }

    public void setMessagesReceived(int messagesReceived) {
        this.messagesReceived = messagesReceived;
    }

    private int messagesStored;

    public int getMessagesStored() {
        return messagesStored;
    }

    public void setMessagesStored(int messagesStored) {
        this.messagesStored = messagesStored;
    }

    private int mailSent;

    public int getMailSent() {
        return mailSent;
    }

    public void setMailSent(int mailSent) {
        this.mailSent = mailSent;
    }

    private int smsSent;

    public int getSmsSent() {
        return smsSent;
    }

    public void setSmsSent(int smsSent) {
        this.smsSent = smsSent;
    }

    private int geocoderRequests;

    public int getGeocoderRequests() {
        return geocoderRequests;
    }

    public void setGeocoderRequests(int geocoderRequests) {
        this.geocoderRequests = geocoderRequests;
    }

    private int geolocationRequests;

    public int getGeolocationRequests() {
        return geolocationRequests;
    }

    public void setGeolocationRequests(int geolocationRequests) {
        this.geolocationRequests = geolocationRequests;
    }

}
