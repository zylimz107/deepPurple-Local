import React, { useState, useEffect, useCallback } from 'react';
import CreateUserDialog from "@/components/AdminDashboard/CreateUserDialog";
import UsersTable from "@/components/AdminDashboard/UsersTable";

const AdminDashboardPage = () => {

    return (
        <>
        <CreateUserDialog />
        <UsersTable />
        </>
    )

}

export default AdminDashboardPage;