package com.alan.wallet.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.alan.wallet.bean.Wallet;

import com.alan.wallet.greendao.WalletDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig walletDaoConfig;

    private final WalletDao walletDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        walletDaoConfig = daoConfigMap.get(WalletDao.class).clone();
        walletDaoConfig.initIdentityScope(type);

        walletDao = new WalletDao(walletDaoConfig, this);

        registerDao(Wallet.class, walletDao);
    }
    
    public void clear() {
        walletDaoConfig.clearIdentityScope();
    }

    public WalletDao getWalletDao() {
        return walletDao;
    }

}
