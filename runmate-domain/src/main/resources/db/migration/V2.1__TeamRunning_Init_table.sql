create table team(
    id bigint primary key auto_increment,
    title varchar(255) not null,
    goal_total_distance float (10,3) not null,
    goal_running_seconds bigint not null,
    result_total_distance float(10,3) not null,
    result_total_running_seconds bigint not null,
    complete_status varchar(10) default 'FAIL',
    goal_started_at timestamp default CURRENT_TIMESTAMP
)default character set utf8;

create table team_member(
    id bigint primary key auto_increment,
    crew_user_id bigint not null,
    team_id bigint not null
)default character set utf8;
alter table team_member
    ADD FOREIGN KEY(crew_user_id) REFERENCES crew_user(id);
alter table team_member
    ADD FOREIGN KEY(team_id) REFERENCES team(id);