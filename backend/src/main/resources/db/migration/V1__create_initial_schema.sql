CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE demo_sessions (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
                               created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE experiments (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                             demo_session_id UUID REFERENCES demo_sessions(id) ON DELETE CASCADE,
                             name VARCHAR(255) NOT NULL,
                             description TEXT,
                             hypothesis TEXT NOT NULL,
                             primary_metric VARCHAR(50) NOT NULL DEFAULT 'CONVERSION_RATE',
                             status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
                             created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT chk_experiment_owner CHECK (
                                 (user_id IS NOT NULL AND demo_session_id IS NULL)
                                     OR
                                 (user_id IS NULL AND demo_session_id IS NOT NULL)
                                 ),

                             CONSTRAINT chk_primary_metric CHECK (
                                 primary_metric IN (
                                                    'CONVERSION_RATE',
                                                    'CLICK_THROUGH_RATE',
                                                    'CLICKS',
                                                    'CONVERSIONS'
                                     )
                                 ),

                             CONSTRAINT chk_experiment_status CHECK (
                                 status IN ('DRAFT', 'RUNNING', 'PAUSED', 'COMPLETED')
                                 )
);

CREATE TABLE variants (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          experiment_id UUID NOT NULL REFERENCES experiments(id) ON DELETE CASCADE,
                          name VARCHAR(100) NOT NULL,
                          description TEXT,
                          configuration JSONB NOT NULL DEFAULT '{}',
                          traffic_percentage NUMERIC(5,2) NOT NULL,

                          CONSTRAINT chk_traffic_percentage CHECK (
                              traffic_percentage > 0 AND traffic_percentage <= 100
                              ),
                        CONSTRAINT uq_variant_experiment UNIQUE (id, experiment_id)
);

CREATE TABLE simulations (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             experiment_id UUID NOT NULL REFERENCES experiments(id) ON DELETE CASCADE,
                             status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                             target_users INTEGER NOT NULL,
                             users_processed INTEGER NOT NULL DEFAULT 0,
                             started_at TIMESTAMP WITH TIME ZONE,
                             completed_at TIMESTAMP WITH TIME ZONE,
                             created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT chk_simulation_status CHECK (
                                 status IN (
                                            'PENDING',
                                            'RUNNING',
                                            'PAUSED',
                                            'COMPLETED',
                                            'FAILED',
                                            'STOPPED'
                                     )
                                 ),

                             CONSTRAINT chk_target_users CHECK (target_users > 0),

                             CONSTRAINT chk_users_processed CHECK (
                                 users_processed >= 0 AND users_processed <= target_users
                                 ),
                            CONSTRAINT uq_simulation_experiment UNIQUE (id, experiment_id)
);

CREATE TABLE assignments (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             simulation_id UUID NOT NULL,
                             variant_id UUID NOT NULL,
                             experiment_id UUID NOT NULL,
                             simulated_user_id UUID NOT NULL,
                             assigned_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT uq_simulation_user
                                 UNIQUE (simulation_id, simulated_user_id),

                             CONSTRAINT fk_assignment_simulation
                                 FOREIGN KEY (simulation_id, experiment_id)
                                     REFERENCES simulations(id, experiment_id)
                                     ON DELETE CASCADE,

                             CONSTRAINT fk_assignment_variant
                                 FOREIGN KEY (variant_id, experiment_id)
                                     REFERENCES variants(id, experiment_id)
                                     ON DELETE CASCADE
);

CREATE TABLE events (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        assignment_id UUID NOT NULL REFERENCES assignments(id) ON DELETE CASCADE,
                        event_type VARCHAR(50) NOT NULL,
                        occurred_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT chk_event_type CHECK (
                            event_type IN ('EXPOSURE', 'CLICK', 'CONVERSION')
                            )
);

CREATE INDEX idx_events_assignment_id
    ON events(assignment_id);

CREATE INDEX idx_events_type_occurred_at
    ON events(event_type, occurred_at);

CREATE INDEX idx_variants_experiment_id
    ON variants(experiment_id);

CREATE INDEX idx_simulations_experiment_id
    ON simulations(experiment_id);