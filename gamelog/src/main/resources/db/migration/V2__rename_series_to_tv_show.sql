
ALTER TABLE series RENAME TO tv_shows;

ALTER TABLE tvseries_vod_providers RENAME TO tv_show_vod_providers;
ALTER TABLE tv_show_vod_providers RENAME COLUMN tvseries_id TO tv_show_id;
ALTER TABLE tv_show_vod_providers RENAME COLUMN provider_name TO provider;

ALTER TABLE seasons RENAME COLUMN series_id TO tv_show_id;
ALTER TABLE seasons ADD COLUMN updated_at TIMESTAMP;