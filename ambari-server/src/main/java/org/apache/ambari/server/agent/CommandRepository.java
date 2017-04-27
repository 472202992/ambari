/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ambari.server.orm.entities.RepositoryEntity;
import org.apache.ambari.server.state.RepositoryInfo;

import com.google.gson.annotations.SerializedName;

/**
 * Wraps the information required to create repositories from a command.  This was added
 * as a top level command object.
 */
public class CommandRepository {

  @SerializedName("repositories")
  private List<Repository> m_repositories = new ArrayList<>();

  @SerializedName("repoVersion")
  private String m_repoVersion;

  @SerializedName("repoVersionId")
  private long m_repoVersionId;

  @SerializedName("stackName")
  private String m_stackName;

  /**
   * @param version the repo version
   */
  public void setRepositoryVersion(String version) {
    m_repoVersion = version;
  }

  /**
   * @param id the repository id
   */
  public void setRepositoryVersionId(long id) {
    m_repoVersionId = id;
  }

  /**
   * @param name the stack name
   */
  public void setStackName(String name) {
    m_stackName = name;
  }

  /**
   * @param repositories the repositories if sourced from the stack instead of the repo_version.
   */
  public void setRepositories(Collection<RepositoryInfo> repositories) {
    m_repositories = new ArrayList<>();

    for (RepositoryInfo info : repositories) {
      m_repositories.add(new Repository(info));
    }
  }

  /**
   * @param osType        the OS type for the repositories
   * @param repositories  the repository entities that should be processed into a file
   */
  public void setRepositories(String osType, Collection<RepositoryEntity> repositories) {
    m_repositories = new ArrayList<>();

    for (RepositoryEntity entity : repositories) {
      m_repositories.add(new Repository(osType, entity));
    }
  }

  /**
   * @return the repositories that the command should process into a file.
   */
  public Collection<Repository> getRepositories() {
    return m_repositories;
  }

  /**
   * Sets a uniqueness on the repo ids.
   *
   * @param suffix  the repo id suffix
   */
  public void setUniqueSuffix(String suffix) {
    for (Repository repo : m_repositories) {
      repo.m_repoId = repo.m_repoId + suffix;
    }
  }

  /**
   * Minimal information required to generate repo files on the agent.  These are copies
   * of the repository objects from repo versions that can be changed for URL overrides, etc.
   */
  public static class Repository {

    @SerializedName("baseUrl")
    private String m_baseUrl;

    @SerializedName("repoId")
    private String m_repoId;

    /**
     * The name should not change.  Ubuntu requires that it match exactly as the repo was built.
     */
    @SerializedName("repoName")
    private final String m_repoName;

    @SerializedName("mirrorsList")
    private String m_mirrorsList;

    private transient String m_osType;

    private Repository(RepositoryInfo info) {
      m_baseUrl = info.getBaseUrl();
      m_osType = info.getOsType();
      m_repoId = info.getRepoId();
      m_repoName = info.getRepoName();
      m_mirrorsList = info.getMirrorsList();
    }

    private Repository(String osType, RepositoryEntity entity) {
      m_baseUrl = entity.getBaseUrl();
      m_repoId = entity.getRepositoryId();
      m_repoName = entity.getName();
      m_mirrorsList = entity.getMirrorsList();
      m_osType = osType;
    }

    public void setBaseUrl(String url) {
      m_baseUrl = url;
    }

    public String getOsType() {
      return m_osType;
    }

    public String getRepoId() {
      return m_repoId;
    }

    public String getRepoName() {
      return m_repoName;
    }


    public String getBaseUrl() {
      return m_baseUrl;
    }
  }

}